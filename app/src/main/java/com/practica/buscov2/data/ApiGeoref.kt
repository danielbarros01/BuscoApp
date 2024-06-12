package com.practica.buscov2.data

import com.practica.buscov2.model.georef.DepartamentosContainer
import com.practica.buscov2.model.georef.LocalidadesContainer
import com.practica.buscov2.model.georef.ProvinciasContainer
import com.practica.buscov2.util.ConstantsGeoref
import com.practica.buscov2.util.ConstantsGeoref.Companion.ENDPOINT_DEPARTAMENTOS
import com.practica.buscov2.util.ConstantsGeoref.Companion.ENDPOINT_LOCALIDADES
import com.practica.buscov2.util.ConstantsGeoref.Companion.ENDPOINT_PROVINCIAS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiGeoref {
    @GET(ENDPOINT_PROVINCIAS)
    suspend fun getProvincias(): Response<ProvinciasContainer>

    @GET(ENDPOINT_DEPARTAMENTOS)
    suspend fun getDepartamentos(@Query("provincia") provinceName: String): Response<DepartamentosContainer>

    @GET(ENDPOINT_LOCALIDADES)
    suspend fun getLocalidades(
        @Query("provincia") provinceName: String,
        @Query("departamento") departmentName: String
    ): Response<LocalidadesContainer>
}