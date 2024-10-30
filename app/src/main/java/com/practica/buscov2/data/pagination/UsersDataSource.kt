package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker

class UsersDataSource (
    private val repo: WorkersRepository,
    tokenP: String,
    ubicationP: LatLng? = null,
) : PagingSource<Int, Worker>() {
    val token = tokenP
    val ubication = ubicationP

    //controla el estado de paginacion
    override fun getRefreshKey(state: PagingState<Int, Worker>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    // Trae más datos cuando se requiere una nueva página
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Worker> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.getRecommendedWorkers(
                token,
                page = nextPageNumber,
                pageSize = 6,
                ubication?.latitude,
                ubication?.longitude
            )
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}