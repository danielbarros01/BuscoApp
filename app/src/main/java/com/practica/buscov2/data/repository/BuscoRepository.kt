package com.practica.buscov2.data.repository

import android.util.Log
import com.google.gson.Gson
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.ErrorBusco
import com.practica.buscov2.model.LoginRequest
import com.practica.buscov2.model.LoginResult
import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.model.RegisterRequest
import com.practica.buscov2.model.User
import com.practica.buscov2.model.UserResult
import com.practica.buscov2.util.AppUtils.Companion.convertToIsoDate
import javax.inject.Inject

class BuscoRepository @Inject constructor(private val api: ApiBusco) {
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

    suspend fun getMyProfile(token: String): UserResult {
        val response = api.getMyProfile("Bearer $token")

        return if (response.isSuccessful) {
            UserResult.Success(
                response.body()?.copy()
                    ?: return UserResult.Error(
                        ErrorBusco(0, "Error", message = "Error desconocido")
                    )
            ) //si hay un cuerpo devuelvo el usuario, si no un error
        } else {
            UserResult.Error(ErrorBusco(0, "Error", message = "Error desconocido"))
        }
    }

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

    suspend fun updateUser(token: String, user: User): Any {
        try {
            //Cambio el de dd/MM/yyyy a yyyy-MM-dd, asi lo pide el servidor
            user.birthdate = user.birthdate?.let { convertToIsoDate(it, "dd/MM/yyyy") }
            val response = api.updateUser("Bearer $token", user)

            return if (!response.isSuccessful) {
                gsonError(response)
            } else {
                true
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
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

    //Cambiar password
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


    private fun gsonError(response: retrofit2.Response<*>): ErrorBusco {
        val errorBody = response.errorBody()
        val gson = Gson()
        val errorResponse: ErrorBusco =
            gson.fromJson(errorBody?.charStream(), ErrorBusco::class.java)

        return errorResponse
    }
}