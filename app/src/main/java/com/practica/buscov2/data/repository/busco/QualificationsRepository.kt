package com.practica.buscov2.data.repository.busco

import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.ListQualification
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.util.ServerUtils
import kotlinx.coroutines.delay
import javax.inject.Inject

class QualificationsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun createQualification(token: String, qualification: Qualification): Any {
        try {
            val response = api.createQualification("Bearer $token", qualification)

            return when (response.code()) {
                in 200..300 -> true
                in 400..599 -> ServerUtils.gsonError(response)
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        }catch (e:Exception){
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }

    suspend fun getQualifications(
        userId:Int,
        page: Int? = null,
        pageSize: Int? = null,
        stars: Int? = null
    ): ListQualification? {
        delay(2000) //para demostracion
        try {
            val response = api.getQualifications(userId,page, pageSize, stars)
            return response.body()
        } catch (e: Exception) {
            return null
        }
    }
}