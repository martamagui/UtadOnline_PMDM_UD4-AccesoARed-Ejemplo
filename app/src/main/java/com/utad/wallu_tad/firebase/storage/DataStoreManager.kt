package com.utad.wallu_tad.firebase.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WALLUTAD_STORE")

class DataStoreManager(val context: Context) {
    private val emailKey = "EMAIL"
    private val jwtKey = "JWT"
    private val isLoggedKey = "IS_LOGGED"

    private suspend fun putString(key: String, value: String) {
        context.dataStore.edit { editor ->
            editor[stringPreferencesKey(key)] = value
        }
    }

    private suspend fun putBoolean(key: String, value: Boolean) {
        context.dataStore.edit { editor ->
            editor[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun saveUser(email: String, jwt: String) {
        putString(emailKey, email)
        putString(jwtKey, jwt)
        putBoolean(isLoggedKey, true)
    }

    private fun getToken(): String? {
        var jwt: String? = null
        context.dataStore.data.map { preferences ->
            jwt = preferences[stringPreferencesKey(jwtKey)]
        }
        return jwt
    }

    suspend fun isUserLogged(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[booleanPreferencesKey(isLoggedKey)] ?: false
    }

    suspend fun logOut() {
        context.dataStore.edit { editor -> editor.clear() }
    }
}