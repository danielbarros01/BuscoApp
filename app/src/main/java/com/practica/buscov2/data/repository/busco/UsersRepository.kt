package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.UserResult
import com.practica.buscov2.util.AppUtils.Companion.convertToIsoDate
import com.practica.buscov2.util.ServerUtils.Companion.gsonError
import okhttp3.MultipartBody
import javax.inject.Inject


class UsersRepository @Inject constructor(private val api: ApiBusco) {
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

    suspend fun updatePictureProfile(token: String, filePart: MultipartBody.Part): Any{
        try {
            val response = api.updatePhoto("Bearer $token", filePart)

            return when (response.code()) {
                in 200..300 -> true
                in 400..599 -> gsonError(response)
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        }catch (e:Exception){
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }
}