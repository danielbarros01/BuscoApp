package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.google.gson.Gson
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.auth.LoginResult
import com.practica.buscov2.model.busco.auth.LoginToken
import javax.inject.Inject
import com.practica.buscov2.util.ServerUtils.Companion.gsonError


class ConfirmationCodeRepository @Inject constructor(private val api: ApiBusco){

    suspend fun confirmCode(isRegister: Boolean, token: String, code: Int, email: String): Any {
        try {
            val response = if (isRegister) {
                api.confirmRegister("Bearer $token", mapOf("code" to code))
            } else {
                api.confirmCodeForPassword(email, code)
            }

            // Log response code for debugging
            Log.d("API Response", "Response code: ${response.code()}")

            return when (response.code()) {
                200 -> {
                    if (!isRegister) {
                        //Debemos devolver el token para que al cambiar el password tengamos la autenticacion
                        LoginResult(response.body() as LoginToken?, null)
                    } else {
                        //Si es registro, ya tenemos el token, no hace falta devolverlo
                        true
                    }
                }

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

    suspend fun resendCode(token: String): Any {
        val response = api.resendCode("Bearer $token")

        return if (response.code() != 200) {
            gsonError(response)
        } else {
            true
        }
    }

    suspend fun sendCode(email: String): Any {
        val response = api.sendCode(email)

        return if (response.code() == 200) {
            true
        } else {
            gsonError(response)
        }
    }

}