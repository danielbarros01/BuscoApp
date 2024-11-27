package com.practica.buscov2.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    private val _isConnected = MutableStateFlow<Boolean?>(null)
    val isConnected: StateFlow<Boolean?> = _isConnected

    fun checkHealth(onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repository.checkHealth()
            }
            _isConnected.value = response

            onResult(response)
        }
    }
}