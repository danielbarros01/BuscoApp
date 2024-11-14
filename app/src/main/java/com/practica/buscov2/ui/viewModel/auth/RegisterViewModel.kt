package com.practica.buscov2.ui.viewModel.auth

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.data.repository.busco.AuthRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.auth.LoginResult
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.model.busco.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    private val _username: MutableState<String> = mutableStateOf("")
    val username: State<String> = _username

    private val _email: MutableState<String> = mutableStateOf("")
    val email: State<String> = _email

    private val _password: MutableState<String> = mutableStateOf("")
    val password: State<String> = _password

    private val _repeatedPassword: MutableState<String> = mutableStateOf("")
    val repeatedPassword: State<String> = _repeatedPassword

    //habilitar o deshabilitar boton login
    private var _buttonEnabled = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled

    var error by mutableStateOf(ErrorBusco())
        private set

    fun onRegisterChanged(
        username: String,
        email: String,
        password: String,
        repeatedPassword: String
    ) {
        _username.value = username
        _email.value = email.lowercase()
        _password.value = password
        _repeatedPassword.value = repeatedPassword
        _buttonEnabled.value =
            isValidUsername(username) && isValidEmail(email) &&
                    isValidPassword(password) && isValidRepeatedPassword(password, repeatedPassword)
    }

    fun setUsername(username: String, onError: () -> Unit) {
        if (username.contains(" ")) {
            error = error.copy(
                code = 0,
                title = "Nombre de usuario inválido",
                message = "El nombre de usuario no puede contener espacios en blanco"
            )
            return onError()
        }else if (username.length > 18){
            error = error.copy(
                code = 0,
                title = "Nombre de usuario inválido",
                message = "El nombre de usuario no puede tener más de 18 caracteres"
            )
            return onError()
        }else if (!username.matches("^[a-zA-Z0-9._]*$".toRegex())){
            error = error.copy(
                code = 0,
                title = "Nombre de usuario inválido",
                message = "El nombre de usuario no debe contener caracteres especiales"
            )
            return onError()
        }

        _username.value = username.lowercase()
    }

    private fun isValidUsername(username: String): Boolean = username.length >= 4

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(password: String): Boolean = password.length >= 6

    private fun isValidRepeatedPassword(password: String, repeatedPassword: String): Boolean =
        password == repeatedPassword

    fun register(onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: LoginResult? =
                    repo.register(username.value, email.value, password.value)

                if (response?.error != null) {
                    error = error.copy(
                        code = response.error.code,
                        title = response.error.title,
                        message = response.error.message
                    )

                    withContext(Dispatchers.Main) {
                        onError()
                    }
                    return@launch // Exit the coroutine if error occurs
                }

                val loginToken: LoginToken? = response?.loginToken
                storeToken.clearToken()
                if (loginToken != null) storeToken.saveToken(loginToken)

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("Error", "Error: $e")
            }
        }
    }


    fun loginWithGoogle(
        completedTask: Task<GoogleSignInAccount>,
        onError: () -> Unit,
        onSuccess: (String) -> Unit
    ) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Obtener el nombre, el ID de Google y el correo electrónico
            val name = account?.displayName
            val email = account?.email
            val googleId = account?.id

            viewModelScope.launch(Dispatchers.IO) {
                val response: LoginResult? = repo.loginGoogle(
                    User(
                        //Para quitar espacio si existe
                        username = name?.replace(" ", ""),
                        email = email,
                        googleId = googleId.toString()
                    )
                )

                if (response?.error != null) {
                    error = error.copy(
                        code = response.error.code,
                        title = response.error.title,
                        message = response.error.message
                    )

                    onError()
                    return@launch // Exit the coroutine if error occurs
                }

                val loginToken: LoginToken? = response?.loginToken
                if (loginToken != null) storeToken.saveToken(loginToken)

                loginToken?.let {
                    onSuccess(it.token)
                }
            }
        } catch (e: ApiException) {
            Log.w("Error", "signInResult:failed code=" + e.statusCode)
        }
    }
}