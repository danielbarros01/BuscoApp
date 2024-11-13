package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practica.buscov2.data.repository.busco.QualificationsRepository
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.ui.viewModel.QualificationsViewModel

class QualificationsDataSource(
    private val repo: QualificationsRepository,
    userIdP: Int,
    starsP: Int?,
    qualificationsViewModel: QualificationsViewModel
) :
    PagingSource<Int, Qualification>() {
    private val userId = userIdP
    private val stars = starsP
    private val viewModel = qualificationsViewModel

    override fun getRefreshKey(state: PagingState<Int, Qualification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Qualification> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.getQualifications(
                userId,
                page = nextPageNumber,
                pageSize = 6,
                stars
            )

            if (response != null) {
                // Actualiza el ViewModel con average y quantity
                viewModel.updateAverageAndQuantity(response.average ?: 0f, response.quantity ?: 0)
                viewModel.updateRatingFrequencies(response.ratingFrequencies)

                LoadResult.Page(
                    data = response.qualifications, // Datos de la respuesta
                    prevKey = null, // Sin página anterior
                    nextKey = if (response.qualifications.isEmpty()) null else nextPageNumber + 1 // Determina la siguiente clave
                )
            } else {
                LoadResult.Error(Exception("Empty response"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}