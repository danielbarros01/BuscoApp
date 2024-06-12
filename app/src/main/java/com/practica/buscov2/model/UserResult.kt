package com.practica.buscov2.model

sealed class UserResult {
    data class Success(val user: User) : UserResult()
    data class Error(val error: ErrorBusco) : UserResult()
}
