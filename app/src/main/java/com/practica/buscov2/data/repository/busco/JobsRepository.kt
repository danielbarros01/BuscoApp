package com.practica.buscov2.data.repository.busco

import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Proposal
import kotlinx.coroutines.delay
import javax.inject.Inject

class JobsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun getJobs(
        token: String,
        page: Int? = null,
        pageSize: Int? = null,
        finished: Boolean? = null
    ): List<Proposal> {
        delay(2000) //para demostracion
        try {
            val response = api.getJobs("Bearer $token", page, pageSize, finished)
            return response.body() ?: emptyList()
        } catch (e: Exception) {
            return emptyList()
        }
    }
}