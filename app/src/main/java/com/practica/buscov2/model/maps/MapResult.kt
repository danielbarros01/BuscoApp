package com.practica.buscov2.model.maps

data class MapResult(
    val results: List<Results>
)

data class Results(
    val geometry: Geometry,
    val formatted_address: String
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
