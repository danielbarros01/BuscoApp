package com.practica.buscov2.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.pagination.ProposalsDataSource
import com.practica.buscov2.data.pagination.SearchDataSource
import com.practica.buscov2.data.repository.busco.ProfessionsRepository
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.ProfessionCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repoWorkers: WorkersRepository,
    private val repoProfessions: ProfessionsRepository,
    private val repoProposals: ProposalsRepository,
) :
    ViewModel() {
    private val _query = mutableStateOf("")
    val query = _query

    private val _token = MutableStateFlow<String?>(null)
    private val token = _token

    private val _ubication = mutableStateOf<LatLng?>(null)
    var ubication: State<LatLng?> = _ubication

    private val _category = mutableStateOf<ProfessionCategory?>(null)
    private var category: State<ProfessionCategory?> = _category

    private val _stars = mutableStateOf<Int?>(null)
    private var stars: State<Int?> = _stars

    private val _categories = mutableStateOf<List<ProfessionCategory>>(emptyList())
    var categories: State<List<ProfessionCategory>> = _categories

    private val _refreshTriggerWorkers = MutableStateFlow(0)
    private val _refreshTriggerProposals = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val workersPage = _refreshTriggerWorkers.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            SearchDataSource(
                repo = repoWorkers,
                tokenP = token.value!!,
                queryP = query.value,
                categoryIdP = category.value?.id,
                qualificationStarsP = stars.value,
                ubicationP = ubication.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val proposalsPage = _refreshTriggerProposals.flatMapLatest {
        Pager(PagingConfig(pageSize = 6)) {
            ProposalsDataSource(
                function = "search",
                repo = repoProposals,
                tokenP = token.value!!,
                queryP = query.value,
                categoryIdP = category.value?.id,
                status = null,
                ubicationP = ubication.value
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshWorkers() {
        _refreshTriggerWorkers.value++
    }

    fun refreshProposals() {
        _refreshTriggerProposals.value++
    }

    fun setUbication(lat: Double, lng: Double) {
        _ubication.value = LatLng(lat, lng)
    }

    fun setToken(token: String) {
        _token.value = token
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun setCategory(newCategory: ProfessionCategory?) {
        _category.value = newCategory
    }

    fun setStars(value:Int?){
        _stars.value = value
    }

    init {
        fetchCategories()
    }

    fun resetValues(){
        _stars.value = null
        _category.value = null
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repoProfessions.getCategories()
                }

                _categories.value = response ?: emptyList()
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }
    }
}