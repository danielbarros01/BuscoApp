package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.Worker
import kotlinx.coroutines.flow.MutableSharedFlow

class SearchDataSource(
    private val repo: WorkersRepository,
    tokenP: String,
    queryP: String,
    categoryIdP: Int? = null,
    qualificationStarsP: Int? = null,
    ubicationP: LatLng? = null
) : PagingSource<Int, Worker>() {
    private val token = tokenP
    private val query = queryP
    private val categoryId = categoryIdP
    private val qualificationStars = qualificationStarsP
    private val ubication = ubicationP

    val totalRecordsFlow = MutableSharedFlow<Int>()

    override fun getRefreshKey(state: PagingState<Int, Worker>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Worker> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.searchWorkers(
                token = token,
                query = query,
                categoryId = categoryId,
                qualificationStars = qualificationStars,
                page = nextPageNumber,
                pageSize = 6,
                lat = ubication?.latitude,
                lng = ubication?.longitude
            )

            // Emitir numberOfRecords en el flujo
            totalRecordsFlow.emit(response.numberOfRecords)

            LoadResult.Page(
                data = response.records,
                prevKey = null,
                nextKey = if (response.records.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}