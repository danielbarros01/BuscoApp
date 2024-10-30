package com.practica.buscov2.model.busco

data class User(
    val id: Int? = null,
    val name: String? = null,
    val lastname: String? = null,
    val username: String? = null,
    val email: String? = null,
    var birthdate: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val image: String? = null,
    val verificationCode: Int? = null,
    val confirmed: Boolean? = null,
    val googleId: String? = null,
    val worker: Worker? = null
)