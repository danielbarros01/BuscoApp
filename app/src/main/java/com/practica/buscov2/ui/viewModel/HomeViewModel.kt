package com.practica.buscov2.ui.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.pagination.ProposalsDataSource
import com.practica.buscov2.data.pagination.UsersDataSource
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.SimpleUbication
import com.practica.buscov2.model.maps.Geolocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repoWorkers: WorkersRepository,
    private val repoProposals: ProposalsRepository,
) : ViewModel() {
    private val _token = MutableStateFlow<String?>(null)
    private val token = _token

    private val _userId = MutableStateFlow<Int?>(null)
    private val userId = _userId

    private val _ubication = mutableStateOf<LatLng?>(null)
    var ubication: State<LatLng?> = _ubication

    private val _refreshTriggerWorkers = MutableStateFlow(0)
    private val _refreshTriggerProposals = MutableStateFlow(0)

    val workersPage = _refreshTriggerWorkers.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            UsersDataSource(repoWorkers, token.value!!, ubicationP = ubication.value)
        }.flow.cachedIn(viewModelScope)
    }


    val proposalsPage = _refreshTriggerProposals.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            //Traer propuestas recomendadas para mi
            ProposalsDataSource(
                repoProposals,
                status = true,
                tokenP = token.value,
                function = "getRecommendedProposals",
                ubicationP = ubication.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshProposals() {
        _refreshTriggerProposals.value++
    }

    fun refreshWorkers() {
        _refreshTriggerWorkers.value++
    }

    fun setToken(token: String) {
        _token.value = token
    }

    fun setUbication(lat: Double, lng: Double) {
        _ubication.value = LatLng(lat, lng)
    }
}