package com.practica.buscov2.ui.viewModel.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repo: UsersRepository,
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun getMyProfile(token: String, onError: (ErrorBusco) -> Unit, onSuccess: (User) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getMyProfile(token)

            withContext(Dispatchers.Main) {
                when (result) {
                    //Si tenemos un usuario
                    is UserResult.Success -> {
                        onSuccess(result.user)
                    }

                    //Si tenemos un error
                    is UserResult.Error -> {
                        val error = ErrorBusco(title = result.error.title, message = result.error.message)
                        onError(error)
                    }
                }
            }
        }
    }
}