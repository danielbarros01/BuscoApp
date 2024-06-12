package com.practica.buscov2.data.repository

import android.util.Log
import com.practica.buscov2.data.ApiGeoref
import com.practica.buscov2.model.georef.Departamento
import com.practica.buscov2.model.georef.Localidad
import com.practica.buscov2.model.georef.Provincia
import javax.inject.Inject

class GeorefRepository @Inject constructor(private val apiGeoref: ApiGeoref) {
    suspend fun getProvincias(): List<Provincia>? {
        val response = apiGeoref.getProvincias()

        if (response.isSuccessful) {
            return response.body()?.provincias
        }

        return null
    }

    suspend fun getDepartamentos(provinceName: String): List<Departamento>? {
        try {
            val response = apiGeoref.getDepartamentos(provinceName)

            if (response.isSuccessful) {
                return response.body()?.departamentos
            }

            //     return null
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
        return null
    }

    suspend fun getLocalidades(provinceName: String, departmentName: String): List<Localidad>? {
        try {
            val response = apiGeoref.getLocalidades(provinceName, departmentName)

            if (response.isSuccessful) {
                return response.body()?.localidades_censales
            }

            //     return null
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
        return null
    }
}


