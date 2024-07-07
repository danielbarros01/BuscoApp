package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.ProfessionCategory
import com.practica.buscov2.model.busco.Worker
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

    suspend fun getWorker(id: Int): Worker? {
        try {
            val response = api.getWorker(id)

            if(response.isSuccessful){
                return response.body()
            }

            return null
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            return null;
        }
    }
}