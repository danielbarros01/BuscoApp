package com.practica.buscov2.model.georef

data class DepartamentosContainer(
    //val cantidad: Int,
    val departamentos: List<Departamento>
)


data class Departamento(
    val id: String,
    val nombre: String
)
