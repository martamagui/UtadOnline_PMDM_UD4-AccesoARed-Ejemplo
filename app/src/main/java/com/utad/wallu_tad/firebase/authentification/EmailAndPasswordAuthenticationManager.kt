package com.utad.wallu_tad.firebase.authentification

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailAndPasswordAuthenticationManager {
    private val auth = Firebase.auth

    fun isUserLogged(): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("FirebaseEmailAndPassAuth", "usuario logeado")
        }
        return currentUser != null
    }


    fun signInFirebaseEmailAndPassword(email: String, password: String): Boolean {
        val result = auth.signInWithEmailAndPassword(email, password)
        if (result.isSuccessful) {
            Log.d("FirebaseAuth", "signInFirebaseEmailAndPassword:success")
            return true
        } else {
            Log.d("FirebaseAuth", "signInFirebaseEmailAndPassword:failure", result.exception)
            return false
        }
    }

    fun createUserFirebaseEmailAndPassword(email: String, password: String): Boolean {
        val result = auth.createUserWithEmailAndPassword(email, password)
        if (result.isSuccessful) {
            Log.d("FirebaseAuth", "createUserFirebaseEmailAndPassword:success")
            return true
        } else {
            Log.d("FirebaseAuth", "createUserFirebaseEmailAndPassword:failure", result.exception)
            return false
        }
    }

    fun signOut() {
        auth.signOut()
    }
}