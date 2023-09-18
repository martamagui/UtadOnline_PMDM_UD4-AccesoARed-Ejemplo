package com.utad.wallu_tad.firebase.cloud_storage

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await


class CloudStorageManager {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    //Esta función retornará el enlace de descarga de la imagen
    suspend fun uploadAdvertisementImage(uri: Uri): String? {
        //Generamos un nombre para la imagen con el Uri quitando todos los / del String
        // para que no nos cree subcarpetas en Firebase
        val imageName = "$uri".replace("/", "_")
        //Creamos una variable para almacenar el enlace de la imagen.
        var imageUrl: String? = null
        //Creamos la referencia dentro de la carpeta de advertisement a nuestra imagen
        val advertisementReference = storageReference.child("advertisement/$imageName")
        //Retornamos la tarea para que la vista pueda escuchar su finalización.
        advertisementReference.putFile(uri).continueWithTask { task ->
            // Si no se ha podido subir la foto lanzamos la excepción que recibamos
            if (!task.isSuccessful) {
                Log.e("CloudStorageManager", "No se ha podido subir la foto")
                task.exception?.let { throw it }
            }
            //Añadimos a al enlace de referencia el link de descarga/visualización
            // a la task para poder recuperarlo en el onCompleteListener
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
        return imageUrl
    }

    suspend fun deleteImage(reference: String): Boolean {
        val advertisementReference = storageReference.child(reference)
        var wasSuccess = true
        advertisementReference.delete().addOnFailureListener {
            wasSuccess = false
        }.await()
        return wasSuccess
    }
}


