package com.practica.buscov2.ui.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.BuscoRepository
import com.practica.buscov2.model.ErrorBusco
import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.model.User
import com.practica.buscov2.model.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val repo: BuscoRepository,
) : ViewModel() {

}