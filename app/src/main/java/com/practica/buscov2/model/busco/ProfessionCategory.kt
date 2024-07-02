package com.practica.buscov2.model.busco

data class ProfessionCategory(
    val id: Int,
    val name: String,
    val professions: List<Profession>?
)