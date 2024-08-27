package com.practica.buscov2.model.busco

data class Message(
    val id: Int? = null,
    val userIdSender: Int,
    val userIdReceiver: Int,
    val text: String,
    val dateAndTime: String? = null,

    val userSender: User? = null
)