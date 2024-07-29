package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker

class SearchDataSource(
    private val repo: WorkersRepository,
    tokenP: String,
    queryP: String,
    cityP: String? = null,
    departmentP: String? = null,
    provinceP: String? = null,
    categoryIdP: Int? = null,
    qualificationStarsP: Int? = null
) : PagingSource<Int, Worker>() {
    private val token = tokenP
    private val query = queryP
    private val city = cityP
    private val department = departmentP
    private val province = provinceP
    private val categoryId = categoryIdP
    private val qualificationStars = qualificationStarsP

    override fun getRefreshKey(state: PagingState<Int, Worker>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: PagingSource.LoadParams<Int>): PagingSource.LoadResult<Int, Worker> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.searchWorkers(
                token = token,
                query = query,
                city,
                department,
                province,
                categoryId,
                qualificationStars,
                page = nextPageNumber,
                pageSize = 6
            )

            PagingSource.LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            PagingSource.LoadResult.Error(e)
        }
    }
}