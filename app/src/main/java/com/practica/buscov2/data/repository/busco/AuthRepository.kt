package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.google.gson.Gson
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.auth.LoginRequest
import com.practica.buscov2.model.busco.auth.LoginResult
import com.practica.buscov2.model.busco.auth.RegisterRequest
import com.practica.buscov2.model.busco.User
import javax.inject.Inject


class AuthRepository @Inject constructor(private val api:ApiBusco){

    suspend fun login(email: String, password: String): LoginResult? {
        val response = api.login(LoginRequest(email, password))

        return when (response.code()) {
            200 -> if (response.isSuccessful) response.body()
                ?.let { LoginResult(it, null) } else LoginResult(
                null,
                ErrorBusco(0, "Error", message = "Error desconocido")
            )

            401 -> LoginResult(
                null,
                ErrorBusco(
                    401,
                    title = "Error de autenticación",
                    message = "Usuario o contraseña incorrectos. Por favor, intente de nuevo."
                )
            )

            else -> LoginResult(null, ErrorBusco(0, "Error", message = "Error desconocido"))
        }
    }

    suspend fun register(username: String, email: String, password: String): LoginResult? {
        val response = api.register(RegisterRequest(username, email, password))

        return when (response.code()) {
            200 -> if (response.isSuccessful) response.body()
                ?.let { LoginResult(it, null) } else LoginResult(
                null,
                ErrorBusco(0, "Error", message = "Error desconocido")
            )

            400 -> {
                val gson = Gson()
                val errorBody = response.errorBody()
                    ?: return LoginResult(
                        null,
                        ErrorBusco(0, "Error", message = "Error desconocido")
                    )
                val errorResponse: List<ErrorBusco> =
                    gson.fromJson(errorBody.string(), Array<ErrorBusco>::class.java).toList()

                LoginResult(
                    null,
                    ErrorBusco(
                        400,
                        title = "Error al registrarse",
                        message = errorResponse.first().message
                    )
                )
            }

            else -> LoginResult(null, ErrorBusco(0, "Error", message = "Error desconocido"))
        }
    }

    suspend fun loginGoogle(user: User): LoginResult? {
        val response = api.googleLogin(user)

        return when (response.code()) {
            200 -> if (response.isSuccessful) response.body()
                ?.let { LoginResult(it, null) } else LoginResult(
                null,
                ErrorBusco(0, "Error", message = "Error desconocido")
            )

            500 -> LoginResult(
                null,
                ErrorBusco(
                    401,
                    title = "Error en la autenticación",
                    message = "Al parecer el error es nuestro, por favor, intentalo de nuevo más tarde."
                )
            )

            else -> LoginResult(null, ErrorBusco(0, "Error", message = "Error desconocido"))
        }
    }

    suspend fun changePassword(token: String, password: String): Any {

        try {
            val response = api.changePassword("Bearer $token", password)

            return when (response.code()) {
                200 -> true
                400 -> {
                    val errorBody = response.errorBody()
                    val gson = Gson()
                    val errorResponse: ErrorBusco =
                        gson.fromJson(errorBody?.charStream(), ErrorBusco::class.java)
                    ErrorBusco(errorResponse.code, "Error", message = errorResponse.message)
                }

                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }

}