package com.practica.buscov2.data.repository

import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.ErrorBusco
import com.practica.buscov2.model.LoginRequest
import com.practica.buscov2.model.LoginResult
import javax.inject.Inject

class BuscoRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun login(email: String, password: String): LoginResult? {
        val response = api.login(LoginRequest(email, password))

        return when (response.code()) {
            200 -> if(response.isSuccessful) response.body()?.let { LoginResult(it, null) } else LoginResult(null, ErrorBusco(0, "Error", "Error desconocido"))
            401 -> LoginResult(null, ErrorBusco(401, title = "Error de autenticación", "Usuario o contraseña incorrectos. Por favor, intente de nuevo."))
            else -> LoginResult(null, ErrorBusco(0, "Error","Error desconocido"))
        }
    }
}