package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.ProfessionCategory
import javax.inject.Inject

class ProfessionsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun getProfession(id: Int): Profession?{
        val response = api.getProfession(id)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }

    suspend fun getProfessions(categoryId: Int): List<Profession>? {
        val response = api.getProfessionsForCategory(categoryId)

        if (response.isSuccessful) {
            return response.body()?.professions
        }

        return null
    }

    suspend fun getProfessionsSearch(query:String): List<Profession>? {
        try {
            val response = api.getProfessionsSearch(query)

            if(response.isSuccessful){
                return response.body()
            }

            return null
        }catch (e:Exception){
            Log.d("Error", e.message.toString())
            return null
        }
    }

    suspend fun getCategories(): List<ProfessionCategory>? {
        val response = api.getCategories()

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}