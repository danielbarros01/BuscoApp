package com.practica.buscov2.ui.viewModel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.model.LoginResult
import com.practica.buscov2.model.LoginToken
import com.practica.buscov2.data.repository.BuscoRepository
import com.practica.buscov2.model.ErrorBusco
import com.practica.buscov2.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: BuscoRepository,
    private val storeToken: StoreToken
) : ViewModel() {

    //Start

    private val _email: MutableState<String> = mutableStateOf("")
    val email: State<String> = _email

    private val _password: MutableState<String> = mutableStateOf("")
    val password: State<String> = _password

    //habilitar o deshabilitar boton login
    private var _loginEnabled = mutableStateOf(false)
    val loginEnabled: State<Boolean> = _loginEnabled

    //Para el progressIndicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    var error by mutableStateOf(ErrorBusco())
        private set

    //Cambiar los valores
    fun onLoginChanged(email: String, password: String) {
        _email.value = email //siempre va a tener el ultimo valor real del textfield
        _password.value = password
        _loginEnabled.value = isValidEmail(email) && isValidPassword(password)
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches();

    private fun isValidPassword(password: String): Boolean = password.length >= 6;

    //api
    fun login(onError: () -> Unit, onSuccess: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Activo el loading
                _isLoading.value = true

                val response: LoginResult? = repo.login(email.value, password.value)

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

                //Asi obtenemos el token
                //val token = storeToken.getTokenFun().firstOrNull()

                loginToken?.let { onSuccess(it.token) }
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