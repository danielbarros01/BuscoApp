package com.practica.buscov2.data.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.practica.buscov2.data.repository.busco.NotificationsRepository
import com.practica.buscov2.model.busco.Notification

class NotificationsDataSource(
    private val repo: NotificationsRepository,
    tokenP: String,
) : PagingSource<Int, Notification>() {
    private val token = tokenP

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = repo.getNotifications(token, page = nextPageNumber, pageSize = 10)
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