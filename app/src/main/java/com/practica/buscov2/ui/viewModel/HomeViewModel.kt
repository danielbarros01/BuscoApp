package com.practica.buscov2.ui.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set

    init {
        //Traer al usuario

    }
}