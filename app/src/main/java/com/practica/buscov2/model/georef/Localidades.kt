package com.practica.buscov2.model.georef

data class LocalidadesContainer(
    val cantidad: Int,
    val localidades_censales: List<Localidad>
)


data class Localidad(
    val id: String,
    val nombre: String
)