package com.practica.buscov2.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.practica.buscov2.data.pagination.JobsDataSource
import com.practica.buscov2.data.repository.busco.JobsRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val repo: JobsRepository
) : ViewModel(){
    /*UI*/
    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error
    /*UI*/

    private val _token = MutableStateFlow<String?>(null)
    private val token = _token

    private val _isFinished = MutableStateFlow<Boolean?>(false)
    private val isFinished = _isFinished

    private val _userId = MutableStateFlow<Int?>(null)
    private val userId = _userId

    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val jobsPage = _refreshTrigger.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            JobsDataSource(
                repo,
                token.value,
                isFinished.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val jobsCompletedPage = _refreshTrigger.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            JobsDataSource(
                repo,
                token.value,
                isFinished.value,
                true,
                userId.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshProposals() {
        _refreshTrigger.value++
    }

    fun setToken(token: String) {
        _token.value = token
    }

    fun changeIsFinished(isFinished: Boolean) {
        _isFinished.value = isFinished
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
    }
}