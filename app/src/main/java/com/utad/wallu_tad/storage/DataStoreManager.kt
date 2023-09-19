package com.utad.wallu_tad.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WALLUTAD_STORE")

class DataStoreManager(val context: Context) {
    private val emailKey = "EMAIL"
    private val jwtKey = "JWT"
    private val isLogged = "IS_LOGGED"

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
        putBoolean(isLogged, true)
    }

    private fun getToken(): String? {
        var jwt: String? = null
        context.dataStore.data.map { preferences ->
            jwt = preferences[stringPreferencesKey(jwtKey)]
        }
        return jwt
    }

    suspend fun isUserLogged(): Boolean {
        var isLoggedValue = false
        val preferences = context.dataStore.data.map { editor ->
            return@map editor
        }
        preferences.collect { editor ->
            isLoggedValue = editor[booleanPreferencesKey(isLogged)] == true
        }
        return isLoggedValue
    }

    suspend fun logOut() {
        context.dataStore.edit { editor -> editor.clear() }
    }
}