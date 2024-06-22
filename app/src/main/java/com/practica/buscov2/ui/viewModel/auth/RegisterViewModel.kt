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

    //Para el progressIndicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun onRegisterChanged(
        username: String,
        email: String,
        password: String,
        repeatedPassword: String
    ) {
        _username.value = username
        _email.value = email
        _password.value = password
        _repeatedPassword.value = repeatedPassword
        _buttonEnabled.value =
            isValidUsername(username) && isValidEmail(email) &&
                    isValidPassword(password) && isValidRepeatedPassword(password, repeatedPassword)
    }

    private fun isValidUsername(username: String): Boolean = username.length >= 4

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches();

    private fun isValidPassword(password: String): Boolean = password.length >= 6;

    private fun isValidRepeatedPassword(password: String, repeatedPassword: String): Boolean =
        password == repeatedPassword

    fun register(onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Activo el loading
                _isLoading.value = true

                val response: LoginResult? =
                    repo.register(username.value, email.value, password.value)

                //Si hay un error
                if (response?.error != null) {
                    error = error.copy(
                        code = response.error.code,
                        title = response.error.title,
                        message = response.error.message
                    )

                    //Desactivo el loading
                    _isLoading.value = false
                    withContext(Dispatchers.Main) {
                        onError()
                    }
                    return@launch // Exit the coroutine if error occurs
                }

                //Si no hubo error, se puede ejecutar lo siguiente
                val loginToken: LoginToken? = response?.loginToken

                //Elimino si habia un token anterior
                storeToken.clearToken()

                //Guardar el token
                if (loginToken != null) storeToken.saveToken(loginToken)

                //Asi obtenemos el token
                //val token = storeToken.getTokenFun().firstOrNull()

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("Error", "Error: $e")
            }
        }
        //Desactivo el loading
        _isLoading.value = false
    }


    fun loginWithGoogle(
        completedTask: Task<GoogleSignInAccount>,
        onError: () -> Unit,
        onSuccess: (String) -> Unit
    ) {
        try {
            //Activo el loading
            _isLoading.value = true

            val account = completedTask.getResult(ApiException::class.java)

            // Obtener el nombre, el ID de Google y el correo electrónico
            val name = account?.displayName
            val email = account?.email
            val googleId = account?.id

            viewModelScope.launch(Dispatchers.IO) {
                val response: LoginResult? = repo.loginGoogle(
                    User(
                        //Para quitar espacio si existe
                        username = name?.replace(" ",""),
                        email = email,
                        googleId = googleId.toString()
                    )
                )

                //Si hay un error
                if (response?.error != null) {
                    error = error.copy(
                        code = response.error.code,
                        title = response.error.title,
                        message = response.error.message
                    )

                    //Desactivo el loading
                    _isLoading.value = false

                    onError()
                    return@launch // Exit the coroutine if error occurs
                }

                //Si no hubo error, se puede ejecutar lo siguiente
                val loginToken: LoginToken? = response?.loginToken

                //Guardar el token
                if (loginToken != null) storeToken.saveToken(loginToken)

                loginToken?.let {
                    //Desactivo el loading
                    _isLoading.value = false

                    onSuccess(it.token)
                }
            }
        } catch (e: ApiException) {
            Log.w("Error", "signInResult:failed code=" + e.statusCode)
            //Desactivo el loading
            _isLoading.value = false
        }
    }
}