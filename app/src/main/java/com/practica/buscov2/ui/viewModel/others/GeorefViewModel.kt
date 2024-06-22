package com.practica.buscov2.ui.viewModel.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.others.GeorefRepository
import com.practica.buscov2.model.georef.Provincia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GeorefViewModel @Inject constructor(
    private val repoGeoref: GeorefRepository
) : ViewModel(){
    private val _provincias = MutableStateFlow<List<Provincia>>(emptyList())
    val provincias = _provincias.asStateFlow()


    init{
        fetchProvincias()
    }

    private fun fetchProvincias(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val result = repoGeoref.getProvincias()
                _provincias.value = result ?: emptyList()
            }
        }
    }
}