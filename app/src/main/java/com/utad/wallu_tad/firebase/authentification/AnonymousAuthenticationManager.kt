package com.utad.wallu_tad.firebase.authentification

import android.content.Context
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AnonymousAuthenticationManager(val context: Context) {
    private val auth = Firebase.auth

    fun isUserLogged(): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("FirebaseAuth", "usuario logeado")
        }
        return currentUser != null
    }

    fun signInAnonymously(): Boolean {
        val result = auth.signInAnonymously()
        if (result.isSuccessful) {
            Log.d("FirebaseAuth", "signInAnonymously:success")
            return true
        } else {
            Log.d("FirebaseAuth", "signInAnonymously:failure", result.exception)
            return false
        }
    }
}