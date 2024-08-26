package com.practica.buscov2.ui.viewModel.auth

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.ConfirmationCodeRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecoverPasswordViewModel @Inject constructor(
    private val repo: ConfirmationCodeRepository
) : ViewModel() {
    private val _email: MutableState<String> = mutableStateOf("")
    val email: State<String> = _email

    private val _buttonEnabled: MutableState<Boolean> = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled

    var error by mutableStateOf(ErrorBusco())
        private set

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun onEmailChange(email: String) {
        _email.value = email
        _buttonEnabled.value = isValidEmail(email)
    }

    fun sendCode(onError: () -> Unit, onSuccess: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = repo.sendCode(email.value)

                when (response) {
                    is Boolean -> {
                        if (response) {
                            withContext(Dispatchers.Main) {
                                onSuccess()
                            }
                        }
                    }

                    is ErrorBusco -> {
                        error = error.copy(
                            code = response.code,
                            title = response.title,
                            message = response.message
                        )
                        withContext(Dispatchers.Main) {
                            onError()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("Error al enviar codigo de recuperacion", e.toString())
        }
    }
}