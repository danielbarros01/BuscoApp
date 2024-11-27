package com.practica.buscov2.model.busco.auth

data class LoginRequest(
    val email: String? = null,
    val username:String? = null,
    val password: String
)
