package com.practica.buscov2.model.busco

data class Application(
    val id: Int? = null,
    val workerUserId: Int? = null,
    val proposalId: Int? = null,
    val date: String? = null,
    val status: Boolean? = null,
    val worker: Worker? = null
)