package com.practica.buscov2.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.practica.buscov2.model.LoginToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreToken(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store")
        val TOKEN = stringPreferencesKey("token")
        val TOKEN_EXPIRATION = stringPreferencesKey("token_expiration")
    }

    val getToken : Flow<LoginToken> = getTokenFun()

    suspend fun saveToken(loginToken: LoginToken) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = loginToken.token
            preferences[TOKEN_EXPIRATION] = loginToken.expiration
        }
    }

    fun getTokenFun(): Flow<LoginToken> {
        return context.dataStore.data
            .map { preferences ->
                val token = preferences[TOKEN] ?: ""
                val expiration = preferences[TOKEN_EXPIRATION] ?: ""

                LoginToken(token, expiration)
            }
    }
}






