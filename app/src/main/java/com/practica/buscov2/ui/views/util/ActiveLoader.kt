package com.practica.buscov2.ui.views.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.practica.buscov2.ui.viewModel.LoadingViewModel

class ActiveLoader {
    companion object {
        //Para activar el loader completo la primera vez que traigo datos
        inline fun <reified T : Any> activeLoaderMax(
            itemsPage: LazyPagingItems<T>,
            vmLoading: LoadingViewModel
        ) {
            val loadState = itemsPage.loadState
            val isLoading =
                loadState.refresh is LoadState.Loading || loadState.prepend is LoadState.Loading
            vmLoading.setLoading(isLoading)
        }
    }
}