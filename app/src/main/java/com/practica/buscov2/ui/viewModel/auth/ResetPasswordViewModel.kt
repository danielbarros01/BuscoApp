package com.practica.buscov2.ui.viewModel.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.AuthRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val _password: MutableState<String> = mutableStateOf("")
    val password: State<String> = _password

    private val _repeatPassword: MutableState<String> = mutableStateOf("")
    val repeatPassword: State<String> = _repeatPassword

    private val _buttonEnabled: MutableState<Boolean> = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled

    var error by mutableStateOf(ErrorBusco())
        private set


    //Cambiar los valores
    fun onPasswordChanged(password1: String, password2: String) {
        _password.value = password1
        _repeatPassword.value = password2
        _buttonEnabled.value =
            isValidPassword(password1) && isValidRepeatedPassword(password1, password2)
    }

    private fun isValidPassword(password: String): Boolean = password.length >= 6;

    private fun isValidRepeatedPassword(password: String, repeatedPassword: String): Boolean =
        password == repeatedPassword


    fun changePassword(token: String, onError: () -> Unit, onSuccess: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    error = ErrorBusco()
                }

                val response = repo.changePassword(token, password.value)

                when (response) {
                    is Boolean -> {
                        if (response) {
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        }
                    }

                    is ErrorBusco -> {
                        withContext(Dispatchers.Main) {
                            error = response
                            onError()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            error = ErrorBusco(message = "Ha ocurrido un error inesperado")
        }
    }
}