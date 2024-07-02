package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.google.gson.Gson
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.util.ServerUtils.Companion.gsonError
import javax.inject.Inject

class WorkersRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun createWorker(token: String, worker: Worker): Any {
        try {
            val response = api.addWorker("Bearer $token", worker)

            return when (response.code()) {
                200 -> true
                in 400..599 -> gsonError(response)
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }
}