package com.practica.buscov2.ui.viewModel

import android.os.CountDownTimer
import android.util.Log
import androidx.collection.IntList
import androidx.collection.MutableIntList
import androidx.collection.mutableIntListOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.practica.buscov2.data.repository.BuscoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.dataStore.StoreToken
import com.practica.buscov2.model.ErrorBusco
import com.practica.buscov2.model.LoginResult
import com.practica.buscov2.model.LoginToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CheckEmailViewModel @Inject constructor(
    private val repo: BuscoRepository,
    private val storeToken: StoreToken
) : ViewModel() {
    private var waitingTime = mutableLongStateOf(10) //120''
    var waitingTimeMillis: Long = waitingTime.longValue * 1000

    private val _timer: MutableStateFlow<CountDownTimer?> = MutableStateFlow(null)
    private val _remainingTime = MutableStateFlow<Long?>(null)
    val remainingTime: StateFlow<Long?> = _remainingTime.asStateFlow()

    private var _verificarEnabled = mutableStateOf(false)
    val verificarEnabled: State<Boolean> = _verificarEnabled

    //Para el progressIndicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    var error by mutableStateOf(ErrorBusco())
        private set

    //crear la lista de dígitos y luego inicialízala con cuatro ceros (List(4) { 0 }).
    private val _digits = mutableStateListOf<Int?>().apply { addAll(List(4) { null }) }
    val digits: List<Int?> get() = _digits


    // Obtener la representación en cadena de `digits`
    val digitsString: String
        get() = digits.joinToString(separator = "") { it.toString() }

    fun addDigit(
        n1: Int?,
        n2: Int?,
        n3: Int?,
        n4: Int?
    ) {
        /*for ((index, digit) in _digits.withIndex()) {
            _digits[index] =
                if (index == 0) n1 else if (index == 1) n2 else if (index == 2) n3 else n4
        }*/

        _digits[0] = n1
        _digits[1] = n2
        _digits[2] = n3
        _digits[3] = n4

        _verificarEnabled.value = digits.all { it != null }
    }

    fun startTimer(millisInFuture: Long) {
        _timer.value?.cancel()
        _timer.value = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinshed: Long) {
                _remainingTime.value = millisUntilFinshed
            }

            override fun onFinish() {
                _remainingTime.value = null
            }
        }.start()
    }

    fun stopTimer() {
        _timer.value?.cancel()
        _timer.value = null
    }

    fun validateCode(
        resend: Boolean = true,
        token: String = "",
        code: String,
        email: String = "",
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                withContext(Dispatchers.Main) {
                    //Activo el loading
                    _isLoading.value = true
                }

                val response = repo.confirmCode(resend, token, code.toInt(), email)

                //Si no se pudo confirmar la cuenta
                when (response) {
                    is Boolean -> {
                        if (response) { // si este Boolean es true:
                            withContext(Dispatchers.Main) {
                                _isLoading.value = false
                                onSuccess()
                            }
                        }
                    }

                    is LoginResult -> {
                        //Guardar el token
                        val loginToken: LoginToken? = response.loginToken
                        if (loginToken != null) storeToken.saveToken(loginToken)

                        withContext(Dispatchers.Main) {
                            _isLoading.value = false
                            onSuccess()
                        }
                    }

                    is ErrorBusco -> {
                        error = error.copy(
                            code = response.code,
                            title = response.title,
                            message = response.message
                        )
                        withContext(Dispatchers.Main) {
                            _isLoading.value = false
                            onError()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                //Desactivo el loading
                _isLoading.value = false
            }
        }
    }


    /*
    * resend en caso de que sea para registro
    * send para recuperar contraseña
    * */
    fun resendCode(
        resend: Boolean = true,
        token: String? = "",
        email: String = "",
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //Activo el loading
                _isLoading.value = true

                val response =
                    if (resend) token?.let { repo.resendCode(it) } else repo.sendCode(email)

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

                //Desactivo el loading
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                //Desactivo el loading
                _isLoading.value = false
            }
        }
    }
}