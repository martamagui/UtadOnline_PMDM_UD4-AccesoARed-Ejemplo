package com.utad.wallu_tad.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.utad.wallu_tad.network.model.responses.UserDataResponse
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WALLUTAD_STORE")

class DataStoreManager(val context: Context) {
    private val emailKey = "EMAIL"
    private val jwtKey = "JWT"
    private val userNameKey = "USERNAME"
    private val fullNameKey = "FULLNAME"
    private val userIdKey = "USER_ID"
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

    suspend fun saveUserData(user: UserDataResponse) {
        putString(emailKey, user.email)
        putString(userIdKey, user.id)
        putString(fullNameKey, user.fullName)
        putString(userNameKey, user.userName)
    }

    suspend fun getToken(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[stringPreferencesKey(jwtKey)]
    }

    suspend fun getUserData(): UserDataResponse? {
        val preferences = context.dataStore.data.first()
        val userName = preferences[stringPreferencesKey(userNameKey)] ?: ""
        val fullName = preferences[stringPreferencesKey(fullNameKey)] ?: ""
        val email = preferences[stringPreferencesKey(emailKey)] ?: ""
        val id = preferences[stringPreferencesKey(userIdKey)] ?: ""
        return UserDataResponse(
            userName = userName,
            fullName = fullName,
            email = email,
            id = id
        )
    }

    suspend fun isUserLogged(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[booleanPreferencesKey(isLoggedKey)] ?: false
    }

    suspend fun logOut() {
        context.dataStore.edit { editor -> editor.clear() }
    }

}