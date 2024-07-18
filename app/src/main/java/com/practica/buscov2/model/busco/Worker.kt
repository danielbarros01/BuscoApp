package com.practica.buscov2.model.busco

data class Worker(
    val userId: Int? = null,
    val title: String? = null,
    val yearsExperience: Int? = null,
    var webPage: String? = null,
    val description: String? = null,
    val professionsId: List<Int?>? = null,
    val workersProfessions: List<WorkersProfessions>? = null
)