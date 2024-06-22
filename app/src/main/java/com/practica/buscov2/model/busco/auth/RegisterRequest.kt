package com.practica.buscov2.model.busco.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
