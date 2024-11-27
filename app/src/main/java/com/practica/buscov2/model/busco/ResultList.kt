package com.practica.buscov2.model.busco

data class ResultList<T>(
    val numberOfRecords: Int,
    val records : List<T>
)
