package com.practica.buscov2.util

class ConstantsGeoref {
    companion object{
        const val BASE_URL_GEOREF = "https://apis.datos.gob.ar/georef/api/"
        const val ENDPOINT_PROVINCIAS = "provincias?campos=id,nombre"
        const val ENDPOINT_DEPARTAMENTOS = "departamentos?campos=id,nombre&max=100&"
        const val ENDPOINT_LOCALIDADES = "localidades-censales?campos=id,nombre&max=100&"
    }
}
