package com.practica.buscov2.model.georef

data class ProvinciasContainer(
    //val cantidad: Int,
    val provincias: List<Provincia>
)


data class Provincia(
    val id: String,
    val nombre: String
)
