package com.practica.buscov2.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.model.LoginToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val storeToken: StoreToken
) : ViewModel() {

    private val _token = MutableStateFlow<LoginToken?>(null)
    val token: StateFlow<LoginToken?> = _token

    init {
        observeToken()
    }

    // Observa los cambios en el DataStore y actualiza el StateFlow
    private fun observeToken() {
        viewModelScope.launch(Dispatchers.IO) {
            storeToken.getTokenFlow()
                .catch { e -> Log.e("Error al obtener el token", e.message.toString()) }
                .collect { newToken -> _token.value = newToken }
        }
    }

    fun deleteToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                storeToken.clearToken()
                _token.value = null
            } catch (e: Exception) {
                Log.e("Error al borrar el token", e.message.toString())
            }
        }
    }
}