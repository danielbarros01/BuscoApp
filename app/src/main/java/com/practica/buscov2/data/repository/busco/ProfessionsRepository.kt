package com.practica.buscov2.data.repository.busco

import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.ProfessionCategory
import javax.inject.Inject

class ProfessionsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun getProfessions(categoryId: Int): List<Profession>? {
        val response = api.getProfessionsForCategory(categoryId)

        if (response.isSuccessful) {
            return response.body()?.professions
        }

        return null
    }

    suspend fun getCategories(): List<ProfessionCategory>? {
        val response = api.getCategories()

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}