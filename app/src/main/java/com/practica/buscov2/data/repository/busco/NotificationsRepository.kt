package com.practica.buscov2.data.repository.busco

import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Notification
import javax.inject.Inject

class NotificationsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun getNotifications(
        token: String,
        page: Int? = null,
        pageSize: Int? = null,
    ): List<Notification> {
        try {
            val response = api.getNotifications("Bearer $token", page, pageSize)
            return response.body() ?: emptyList()
        } catch (e: Exception) {
            return emptyList()
        }
    }
}