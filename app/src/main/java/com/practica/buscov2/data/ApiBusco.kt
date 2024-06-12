package com.practica.buscov2.data

import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.model.LoginRequest
import com.practica.buscov2.model.RegisterRequest
import com.practica.buscov2.model.User
import com.practica.buscov2.util.Constants.Companion.CONFIRM_REGISTER
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_LOGIN
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_MY_PROFILE
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_REGISTER
import com.practica.buscov2.util.Constants.Companion.ENDPOINT_USERS
import com.practica.buscov2.util.Constants.Companion.RESEND_CODE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiBusco {
    @POST("$ENDPOINT_USERS/$ENDPOINT_LOGIN")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginToken>

    @POST("$ENDPOINT_USERS/$ENDPOINT_REGISTER")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<LoginToken>

    @GET("$ENDPOINT_USERS/$ENDPOINT_MY_PROFILE")
    suspend fun getMyProfile(@Header("Authorization") token: String): Response<User>

    @PATCH("$ENDPOINT_USERS/$CONFIRM_REGISTER")
    suspend fun confirmRegister(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<Unit>

    @PATCH("$ENDPOINT_USERS/$RESEND_CODE")
    suspend fun resendCode(
        @Header("Authorization") token: String
    ): Response<Unit>

    @PUT(ENDPOINT_USERS)
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<Unit>
}