package com.utad.wallu_tad.firebase.db

import com.google.firebase.database.FirebaseDatabase
import com.utad.wallu_tad.firebase.db.model.FavouriteAdvertisement

class RealTimeDatabaseManager {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    fun addFavourite(favourite: FavouriteAdvertisement): FavouriteAdvertisement? {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")". Si quisieramos almacenar
        // más objetos en otras funciones, deberíamos conectarnos a otro child para tener la
        // información separada. Si el nodo no está creado en la base de datos, lo crea al conectarnos
        val connection = databaseReference.child("faves")
        //Creamos una key
        val key = connection.push().key
        //Si no es nula, guardamos el anuncio en favoritos
        if (key != null) {
            //Añadimos a la conexión un objeto hijo con la key que hemos creado
            //y ponemos nuestra dataclass el valor del anuncio marcado como favorito.
            connection.child(key).setValue(favourite)
            //Si todo ha ido bien, retornamos el objeto con su key
            return favourite.copy(key = key)
        } else {
            // Si no hemos podido crear la key retornamos null para saber que no se guardó
            return null
        }
    }

    fun deleteFavourite(favouriteKey: String) {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")
        //Borramos este favorito por su key
        connection.child(favouriteKey).removeValue()
    }

    fun updateFavourite(favouriteKey: String, favourite: FavouriteAdvertisement) {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")
        //Actualizamos este favorito por su key, si os dais cuenta es la misma función
        // que para crear un nuevo dato. Solo que en este caso el key ya existía
        // previamente y sobreescribimos la información
        connection.child(favouriteKey).setValue(favourite)
    }

    //Con esta notación indicamos que la función puede lanzar una excepción
    @Throws
    fun readFavourites(userId: String): List<FavouriteAdvertisement> {
        //Nos conectamos la nodo de "faves" mediante ".child("faves")"
        val connection = databaseReference.child("faves")
        //Creamos y retornamos un flow que estará constantemente escuchando los cambios en la base de datos
        val list = mutableListOf<FavouriteAdvertisement>()
        //Escuchamos la conexión para recoger la lista
        connection.get().addOnSuccessListener { snapshot ->
            val faves: MutableList<FavouriteAdvertisement> = mutableListOf()
            snapshot.children.mapNotNull { dataSnapshot ->
                //Recogemos cada favorito y le asignamos su key si no son nulos
                val favourite = snapshot.getValue(FavouriteAdvertisement::class.java)
                val key = snapshot.key
                if (key != null && favourite != null) {
                    favourite.key = key
                    faves.add(favourite)
                }
            }
            //Filtramos la lista por el id del usuario
            faves.filter { it.userId == userId }
        }.addOnFailureListener {
            //Si falla, lanzamos la excepción que recibamos
            throw it
        }
        return list
    }

}