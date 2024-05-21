package com.practica.buscov2.model

import java.time.LocalDateTime

data class Weatherforecast(
    val date: LocalDateTime,
    val temperatureC: Int, val
    temperatureF: Int,
    val summary: String
)