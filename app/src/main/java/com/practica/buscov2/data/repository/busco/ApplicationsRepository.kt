package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.util.ServerUtils
import kotlinx.coroutines.delay
import javax.inject.Inject

class ApplicationsRepository @Inject constructor(private val api: ApiBusco) {

    suspend fun getApplications(
        token: String,
        proposalId: Int,
        page: Int? = null,
        pageSize: Int? = null,
    ): List<Application> {
        try {
            delay(1000)
            val response =
                api.getApplicationsForProposal("Bearer $token", proposalId, page, pageSize)
            return response.body() ?: emptyList()
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
            return emptyList()
        }
    }

    suspend fun getApplicationAccepted(
        proposalId: Int,
    ): Application? {
        try {
            val response = api.getApplicationAccepted(proposalId)
            return response.body()
        } catch (e: Exception) {
            Log.d("ERROR", e.toString())
            return null
        }
    }

    suspend fun acceptOrDeclineApplication(
        token: String,
        proposalId: Int,
        applicationId: Int,
        status: Boolean
    ):Any {
        try {
            val response = api.acceptOrDeclineApplication("Bearer $token", proposalId, applicationId, status)

            return when (response.code()) {
                in 200..299 -> true
                in 400..599 -> ServerUtils.gsonError(response) //return ErrorBusco
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        }catch (e: Exception){
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }

    suspend fun createApplication(
        token: String,
        proposalId: Int
    ):Any {
        try {
            val response = api.createApplication("Bearer $token", proposalId)

            return when (response.code()) {
                in 200..299 -> true
                in 400..599 -> ServerUtils.gsonError(response) //return ErrorBusco
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        }catch (e: Exception){
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }
}