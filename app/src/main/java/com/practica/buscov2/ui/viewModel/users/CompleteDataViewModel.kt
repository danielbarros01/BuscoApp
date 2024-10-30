package com.practica.buscov2.ui.viewModel.users

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.repository.busco.UsersRepository
import com.practica.buscov2.data.repository.others.GeorefRepository
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.georef.Departamento
import com.practica.buscov2.model.georef.Localidad
import com.practica.buscov2.model.georef.Provincia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CompleteDataViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val repoGeoref: GeorefRepository
) : ViewModel() {
    var user by mutableStateOf(User())
        private set

    //Start
    private val _name: MutableState<String> = mutableStateOf("")
    val name: State<String> = _name

    private val _lastname: MutableState<String> = mutableStateOf("")
    val lastname: State<String> = _lastname

    private var _dateOfBirth = mutableStateOf("")
    val dateOfBirth: State<String> = _dateOfBirth

    private var _buttonNextEnable = mutableStateOf(false)
    val buttonEnable: State<Boolean> = _buttonNextEnable

    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error

    private val _ubication = mutableStateOf<LatLng?>(null)
    var ubication: State<LatLng?> = _ubication

    fun onDateChangedInitializedData(userP: User) {
        viewModelScope.launch {
        }
    }

    //cambiar los valores de ubicacion
    fun changeUbication(lat:Double, lng:Double) {
        user = user.copy(
            latitude = lat,
            longitude = lng
        )

        _buttonNextEnable.value = true
    }

    fun changeEnabledButton(enable: Boolean) {
        _buttonNextEnable.value = enable
    }

    //Cambiar los valores
    fun onDateChanged(name: String, lastname: String, dateOfBirth: String) {
        _name.value = name
        _lastname.value = lastname
        _dateOfBirth.value = dateOfBirth
        _buttonNextEnable.value =
            isValidName(name) && isValidLastName(lastname)
                    && isValidDateOfBirth(dateOfBirth)

        user = user.copy(
            name = name,
            lastname = lastname,
            birthdate = dateOfBirth,
        )
    }

    fun setDateOfBirth(dateOfBirth: String) {
        _dateOfBirth.value = dateOfBirth
        _buttonNextEnable.value =
            isValidName(name.value) && isValidLastName(lastname.value)
                    && isValidDateOfBirth(dateOfBirth)

        user = user.copy(
            birthdate = dateOfBirth,
        )
    }

    fun setError(errorP: ErrorBusco) {
        _error.value = errorP
    }

    private fun isValidName(name: String): Boolean = name.length > 3
    private fun isValidLastName(lastname: String): Boolean = lastname.length > 3
    private fun isValidDateOfBirth(date: String): Boolean = date.isNotEmpty()

    fun saveCompleteData(token: String, userP: User? = user, onError: () -> Unit, onSuccess: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                // Activo el loading
                withContext(Dispatchers.Main) {
                    _error.value = ErrorBusco()
                }

                val response = userP?.let { repo.updateUser(token, it) }

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
                            _error.value = response
                            onError()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")
        }
    }

}