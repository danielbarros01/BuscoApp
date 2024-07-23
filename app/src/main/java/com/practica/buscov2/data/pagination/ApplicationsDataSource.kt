package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practica.buscov2.data.repository.busco.ApplicationsRepository
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.User

class ApplicationsDataSource(
    private val repo: ApplicationsRepository,
    proposalId: Int,
    tokenP: String
) : PagingSource<Int, Application>() {
    val token = tokenP
    val id = proposalId

    override fun getRefreshKey(state: PagingState<Int, Application>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Application> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.getApplications(
                token,
                proposalId = id,
                page = nextPageNumber,
                pageSize = 6
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