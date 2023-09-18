package com.utad.wallu_tad.firebase.cloud_storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await


class CloudStorageManager {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    suspend fun uploadAdvertisementImage(uri: Uri): String? {
        val imageName = "$uri".replace(
            "/",
            "_"
        ) //Generamos un nombre para la imagen a partir de su ruta remplazando los "/" por "_"
        //Creamos una variable para almacenar el enlace de descarga.
        var imageUrl: String? = null
        //Creamos la referencia dentro de la carpeta de advertisement a nuestra imagen
        val advertisementReference = storageReference.child("advertisement/$imageName")

        //Mediante el método .putFile() subimos el archivo a Firebase
        advertisementReference.putFile(uri).continueWithTask { task ->
            // Si no se ha podido subir la foto lanzamos la excepción
            if (!task.isSuccessful) {
                Log.e("CloudStorageManager", "No se ha podido subir la foto")
                task.exception?.let { throw it }
            }
            // Añadimos a la Task enlace de descarga a la task para poder recuperarlo en
            // poder recuperarlo en el onCompleteListener
            advertisementReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Recuperamos el enlace donde se subió la imagen
                imageUrl = "${task.result}"
                Log.i("NewAdv", "Enlace de la foto: $imageUrl")
            } else {
                Log.e("NewAdv", "No se ha podido subir la foto")
            }
        }.await()
        //.await() hará que la función no continue hasta que termine la tarea
        return imageUrl
    }

    suspend fun getAllImages(): List<String> {
        //Accedemos al nodo de las imágenes de los anuncios
        val advertisementReference = storageReference.child("advertisement")
        //Creamos una lista vacía donde añadiremos los enlaces de las fotos
        val imageList = mutableListOf<String>()
        //Pedimos el result de la referencia
        val result: ListResult = advertisementReference.listAll().await()
        //Iteramos la lista de items de la referencida con un forEach
        result.items.forEach { item ->
            //Añadimos a la lista vacía de los enlaces de descarga de cada item
            imageList.add(item.downloadUrl.toString())
        }
        return imageList
    }

    suspend fun deleteImage(url: String): Boolean {
        //Recuperamos la referencia a partir del enlace
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(url)
        //Creamos la variable que devolveremos
        var wasSuccess = true

        if (reference != null) {
            reference.delete().addOnFailureListener {
                // Si falla, devolvemos error
                wasSuccess = false
            }.await()
        } else {
            // Si no hay referencia, devolvemos error
            wasSuccess = false
        }
        return wasSuccess
    }



}


