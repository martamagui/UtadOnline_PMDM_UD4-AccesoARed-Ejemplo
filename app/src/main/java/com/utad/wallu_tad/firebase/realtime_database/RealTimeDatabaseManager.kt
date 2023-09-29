package com.utad.wallu_tad.firebase.realtime_database

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.utad.wallu_tad.firebase.realtime_database.model.FavouriteAdvertisement
import kotlinx.coroutines.tasks.await

class RealTimeDatabaseManager {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    suspend fun addFavourite(favourite: FavouriteAdvertisement): FavouriteAdvertisement? {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")". Si quisieramos almacenar
        // más objetos en otras funciones, deberíamos conectarnos a otro child para tener la
        // información separada. Si el nodo no está creado en la base de datos, lo crea al conectarnos.
        val connection = databaseReference.child("faves")
        //Creamos una key
        val key = connection.push().key
        //Si no es nula, guardamos el anuncio en favoritos
        if (key != null) {
            //Hacemos una copia del favorito asignándole la key
            val favouriteWithKey = favourite.copy(key=key)
            //Añadimos a la conexión un objeto hijo con la key que hemos creado
            //y ponemos nuestra dataclass el valor del anuncio marcado como favorito.
            connection.child("${favourite.advertisementId}").setValue(favouriteWithKey).await()
            //Si todo ha ido bien, retornamos el objeto con su key
            Log.d("addFavourite", "guardado")
            return favouriteWithKey
        } else {
            Log.e("addFavourite", "fallo")
            // Si no hemos podido crear la key retornamos null para saber que no se guardó
            return null
        }
    }

    fun deleteFavourite(favouriteId: String) {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")
        //Borramos este favorito por su id
        connection.child(favouriteId).removeValue()
        Log.i("Fave", "Borrado")
    }

    fun updateFavourite(favourite: FavouriteAdvertisement) {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")
        //Actualizamos este favorito por su id, si os dais cuenta es la misma función
        // que para crear un nuevo dato. Solo que en este caso el key ya existía
        // previamente y sobreescribimos la información
        connection.child(favourite.advertisementId).setValue(favourite)
    }

    suspend fun readFavourite(advertisementId: String): FavouriteAdvertisement? {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")

        val snapshot = connection.get()
        //Esperamos la conexión para recoger la lista
        snapshot.await()

        snapshot.result.children.mapNotNull { dataSnapshot ->
            //Recogemos cada favorito y le asignamos su key si no son nulos
            val favourite = dataSnapshot.getValue(FavouriteAdvertisement::class.java)
            Log.i("Fave", "$favourite")
            if (favourite != null && favourite.advertisementId == advertisementId) {
                //Si encontramos el anuncio que coincida el ID, lo retornamos
                return favourite
            }
        }
        //Si NO encontramos el anuncio que coincida el ID, devolvemos un nulo
        return null
    }

}
