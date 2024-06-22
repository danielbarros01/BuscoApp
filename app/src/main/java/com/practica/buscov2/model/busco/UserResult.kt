package com.practica.buscov2.model.busco

import com.practica.buscov2.model.busco.auth.ErrorBusco

sealed class UserResult {
    data class Success(val user: User) : UserResult()
    data class Error(val error: ErrorBusco) : UserResult()
}
