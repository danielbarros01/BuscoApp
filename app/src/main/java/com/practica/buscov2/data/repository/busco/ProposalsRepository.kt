package com.practica.buscov2.data.repository.busco

import android.util.Log
import com.practica.buscov2.data.ApiBusco
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.util.ServerUtils
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class ProposalsRepository @Inject constructor(private val api: ApiBusco) {
    suspend fun createProposal(
        token: String,
        proposal: Proposal,
        filePart: MultipartBody.Part?
    ): Any {
        try {
            /*Convertir */
            val titleRequestBody = proposal.title?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionRequestBody =
                proposal.description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val requirementsRequestBody =
                proposal.requirements?.toRequestBody("text/plain".toMediaTypeOrNull())
            val minBudgetRequestBody =
                proposal.minBudget?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val maxBudgetRequestBody =
                proposal.maxBudget?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val professionIdRequestBody =
                proposal.professionId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())


            val response = api.createProposal(
                token = "Bearer $token",
                title = titleRequestBody,
                description = descriptionRequestBody,
                requirements = requirementsRequestBody,
                minBudget = minBudgetRequestBody,
                maxBudget = maxBudgetRequestBody,
                image = filePart, // Tu MultipartBody.Part para la imagen
                professionId = professionIdRequestBody
            )

            return when (response.code()) {
                in 200..300 -> true
                in 400..599 -> ServerUtils.gsonError(response) //return ErrorBusco
                else -> ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
            }
        } catch (e: Exception) {
            Log.d("Error", e.toString())
            return ErrorBusco(0, "Error", message = "Error desconocido, intentalo de nuevo")
        }
    }

    suspend fun getProposalsOfUser(
        userId: Int,
        page: Int? = null,
        pageSize: Int? = null,
        status: Boolean? = null
    ): List<Proposal> {
        //delay(3000)
        val response = api.getProposalsOfUser(userId, page, pageSize, status)
        return response.body() ?: emptyList()
    }

    suspend fun getProposal(proposalId: Int): Proposal? {
        val response = api.getProposal(proposalId)

        if (response.isSuccessful) {
            return response.body()
        }

        return null
    }
}