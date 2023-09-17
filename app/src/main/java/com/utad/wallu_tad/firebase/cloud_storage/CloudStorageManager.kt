package com.utad.wallu_tad.firebase.cloud_storage

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class CloudStorageManager {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    //Retornamos una task que podrá escuchar la vista para saber cuando se ha subido la imagen
    fun uploadAdvertisementImage(uri: Uri): Task<Uri> {
        //Generamos un nombre para la imagen con el Uri quitando todos los / del String
        // para que no nos cree subcarpetas en Firebase
        val imageName = "$uri".replace("/", "_")
        //Creamos la referencia dentro de la carpeta de advertisement a nuestra imagen
        val advertisementReference = storageReference.child("advertisement/$imageName")

        //Retornamos la tarea para que la vista pueda escuchar su finalización.
        return advertisementReference.putFile(uri).continueWithTask { task ->
            // Si no se ha podido subir la foto lanzamos la excepción que recibamos
            if (!task.isSuccessful) {
                Log.e("CloudStorageManager", "No se ha podido subir la foto")
                task.exception?.let { throw it }
            }
            //Añadimos a al enlace de referencia el link de descarga/visualización
            // a la task para poder recuperarlo desde la vista
            advertisementReference.downloadUrl
        }
    }

    fun deleteImage(reference: String): Task<Void> {
        val advertisementReference = storageReference.child(reference)
        //Retornamos la tarea de eliminación para que la pueda escuchar la View
        return advertisementReference.delete()
    }
}


