package com.practica.buscov2.data.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreUbication(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data_store_ubication")
        val UBICATION_LAT = doublePreferencesKey("latitude")
        val UBICATION_LNG = doublePreferencesKey("longitude")
    }

    suspend fun saveUbication(latLng: LatLng) {
        context.dataStore.edit { preferences ->
            preferences[UBICATION_LAT] = latLng.latitude
            preferences[UBICATION_LNG] = latLng.longitude
        }
    }

    fun getUbicationFlow(): Flow<LatLng?> {
        return context.dataStore.data
            .map { preferences ->
                val lat = preferences[UBICATION_LAT]
                val lng = preferences[UBICATION_LNG]
                if (lat != null && lng != null) {
                    LatLng(lat, lng)
                } else {
                    null
                }
            }
    }

    suspend fun clearUbication() {
        context.dataStore.edit { preferences ->
            preferences.remove(UBICATION_LAT)
            preferences.remove(UBICATION_LNG)
        }
    }
}