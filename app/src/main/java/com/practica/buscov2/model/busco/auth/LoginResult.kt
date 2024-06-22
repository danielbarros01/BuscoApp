package com.practica.buscov2.model.busco.auth

data class LoginResult(
    val loginToken: LoginToken?,
    val error: ErrorBusco?
)

//El nombre es para no usar alguno reservado
data class ErrorBusco(
    var code: Int = 0,
    var title: String = "Error",
    var field: String = "",
    var message: String = ""
)
