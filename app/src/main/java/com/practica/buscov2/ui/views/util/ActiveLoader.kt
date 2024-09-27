package com.practica.buscov2.ui.views.util

import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ActiveLoader {
    companion object {
        inline fun <reified T : Any> activeLoaderMax(
            itemsPage: LazyPagingItems<T>,
            vmLoading: LoadingViewModel,
            timeoutMillis: Long = 10000L
        ) {
            val loadState = itemsPage.loadState

            val isLoading = loadState.refresh is LoadState.Loading || loadState.prepend is LoadState.Loading

            vmLoading.setLoading(isLoading)

            if (isLoading) {
                vmLoading.viewModelScope.launch {
                    delay(timeoutMillis)
                    vmLoading.hideLoading()
                }
            }
        }
    }
}