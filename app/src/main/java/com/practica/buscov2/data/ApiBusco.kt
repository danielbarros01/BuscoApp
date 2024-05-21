package com.practica.buscov2.data

import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.model.LoginRequest
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_LOGIN
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_USERS
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiBusco {
    @POST("$ENDPOINT_USERS/$ENDPOINT_LOGIN")
    suspend fun login(@Body loginRequest: LoginRequest
    ): Response<LoginToken>

    //@GET("$ENDPOINT_PRUEBA")
    //fun prueba() :

}