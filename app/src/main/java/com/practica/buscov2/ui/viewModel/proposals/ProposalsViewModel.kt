package com.practica.buscov2.ui.viewModel.proposals

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.practica.buscov2.model.busco.Proposal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.practica.buscov2.data.pagination.ProposalsDataSource
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
* _refreshTrigger: Un MutableStateFlow que actúa como disparador para la recarga.
* Cada vez que su valor cambia, el Pager se vuelve a crear, lo que obliga a la
* LazyColumn a recargar los datos desde la primera página.
* */


@HiltViewModel
class ProposalsViewModel @Inject constructor(
    private val repo: ProposalsRepository
) : ViewModel() {
    /*UI*/
    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error

    /*UI*/
    private val _userId = mutableIntStateOf(0)
    val userId: State<Int> = _userId

    private val _status: MutableState<Boolean?> = mutableStateOf(null)
    val status: State<Boolean?> = _status

    private val _proposals = mutableStateOf<List<Proposal>>(emptyList())
    val proposals: State<List<Proposal>> = _proposals

    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val proposalsPage = _refreshTrigger.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            ProposalsDataSource(
                repo,
                userId.value,
                status.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshProposals() {
        _refreshTrigger.value++
    }

    fun changeStatus(status: Boolean? = null) {
        _status.value = status
    }

    fun changeUserId(userId: Int) {
        _userId.intValue = userId
    }
}