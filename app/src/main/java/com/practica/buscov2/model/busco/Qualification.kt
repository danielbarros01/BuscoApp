package com.practica.buscov2.model.busco

data class ListQualification(
    val quantity: Int?,
    val average: Float?,
    val qualifications: List<Qualification>,
    val ratingFrequencies: Map<Int, Int>?
)

data class Qualification(
    val score: Float? = null,
    val quantity: Int? = null,
    val commentary: String? = null,
    val date: String? = null,
    val workerUserId: Int? = null,
    val user: User? = null
)

