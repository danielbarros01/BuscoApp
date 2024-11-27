package com.practica.buscov2.data.repository.busco

import com.practica.buscov2.data.ApiBusco
import javax.inject.Inject

class HealthRepository @Inject constructor(private val api:ApiBusco) {
    suspend fun checkHealth():Boolean{
        try {
            val response = api.checkHealth()
            return response.code() == 200
        }catch (e:Exception){
            return false
        }
    }
}