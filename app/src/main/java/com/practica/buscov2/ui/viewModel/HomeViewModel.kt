package com.practica.buscov2.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.pagination.ProposalsDataSource
import com.practica.buscov2.data.pagination.UsersDataSource
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.data.repository.busco.WorkersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val repoWorkers: WorkersRepository,
    private val repoProposals: ProposalsRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    /*UI*/
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    /*UI*/

    private val _token = MutableStateFlow<String?>(null)
    private val token = _token

    private val _userId = MutableStateFlow<Int?>(null)
    private val userId = _userId

    private val _refreshTriggerWorkers = MutableStateFlow(0)
    private val _refreshTriggerProposals = MutableStateFlow(0)

    val workersPage = _refreshTriggerWorkers.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            //Traer trabajadores recomendadas para mi
            UsersDataSource(repoWorkers, token.value!!)
        }.flow.cachedIn(viewModelScope)
    }

    val proposalsPage = _refreshTriggerProposals.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            //Traer propuestas recomendadas para mi
            ProposalsDataSource(repoProposals, status =  true, tokenP =  token.value, function = "getRecommendedProposals")
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshWorkers() {
        _refreshTriggerWorkers.value++
    }

    fun refreshProposals() {
        _refreshTriggerProposals.value++
    }

    fun setToken(token:String){
        _token.value = token
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }
}