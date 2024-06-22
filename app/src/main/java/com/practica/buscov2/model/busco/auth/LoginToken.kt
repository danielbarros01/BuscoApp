package com.practica.buscov2.model.busco.auth

data class LoginToken(
    val token: String,
    val expiration: String,
)