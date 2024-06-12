package com.practica.buscov2.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.BuscoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: BuscoRepository,
    private val storeToken: StoreToken
) : ViewModel() {


    fun logout(onSuccesss: () -> Unit){
        viewModelScope.launch {
            storeToken.clearToken()
            onSuccesss()
        }
    }
}