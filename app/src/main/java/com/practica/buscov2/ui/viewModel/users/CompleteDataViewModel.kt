package com.practica.buscov2.ui.viewModel.users

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    //Para el progressIndicator
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

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

    //Provincia, Departamento y ciudad
    private val _provincias = MutableStateFlow<List<Provincia>>(emptyList())
    val provincias = _provincias.asStateFlow()

    private val _departamentos = MutableStateFlow<List<Departamento>>(emptyList())
    val departamentos = _departamentos.asStateFlow()

    private val _localidades = MutableStateFlow<List<Localidad>>(emptyList())
    val localidades = _localidades.asStateFlow()

    //view
    private val _pais: MutableState<String> = mutableStateOf("")
    val pais: State<String> = _pais

    private val _provincia: MutableState<String> = mutableStateOf("Seleccione una provincia")
    val provincia: State<String> = _provincia

    private val _departamento: MutableState<String> = mutableStateOf("Seleccione un departamento")
    val departamento: State<String> = _departamento

    private val _localidad: MutableState<String?> = mutableStateOf("Seleccione una localidad")
    val localidad: State<String?> = _localidad

    //En el caso de la ciudad autonoma no tiene localidades, pero si departamentos
    private val _enableChooseCity = mutableStateOf(true)
    val enableChooseCity: State<Boolean> = _enableChooseCity

    init {
        fetchProvincias()
    }

    private fun fetchProvincias() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repoGeoref.getProvincias()
                _provincias.value = result ?: emptyList()
            }
        }
    }

    fun fetchDepartamentos() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repoGeoref.getDepartamentos(provincia.value)
                _departamentos.value = result ?: emptyList()

                _departamento.value = "Seleccione un departamento"
                _localidades.value = emptyList()
                _localidad.value = "Seleccione una localidad"
                _buttonNextEnable.value = false
            }
        }
    }

    fun fetchLocalidades() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repoGeoref.getLocalidades(provincia.value, departamento.value)
                _localidades.value = result ?: emptyList()

                _localidad.value = "Seleccione una localidad"
                if (_enableChooseCity.value) _buttonNextEnable.value = false
            }
        }
    }

    //cambiar los valores de ubicacion
    fun onDateChangedUbication(
        country: String,
        province: String,
        department: String,
        city: String?
    ) {
        viewModelScope.launch {
            _pais.value = country
            _provincia.value = province
            _departamento.value = department

            //CABA no tiene localidades
            if (province.equals("Ciudad Autónoma de Buenos Aires", ignoreCase = true)) {
                _localidad.value = null
                _enableChooseCity.value = false
                _buttonNextEnable.value =
                    isValidProvincia(province) && isValidDepartamento(department)
            } else {
                _localidad.value = city
                _enableChooseCity.value = true
                _buttonNextEnable.value = isValidPais(country) && isValidProvincia(province)
                        && isValidDepartamento(department)
                        && city?.let { isValidCiudad(it) } == true
            }

            user = user.copy(
                country = country, province = province, department = department, city = city
            )
        }
    }

    private fun isValidProvincia(provinceName: String): Boolean {
        val provinceList = _provincias.value // Get the latest list of Localidad objects
        return provinceList.any { province -> province.nombre == provinceName }
    }

    private fun isValidDepartamento(departmentName: String): Boolean {
        val departmentList = _departamentos.value // Get the latest list of Localidad objects
        return departmentList.any { department -> department.nombre == departmentName }
    }

    private fun isValidCiudad(cityName: String): Boolean {
        val localidadesList = _localidades.value // Get the latest list of Localidad objects
        return localidadesList.any { localidad -> localidad.nombre == cityName }
    }

    private fun isValidPais(country: String): Boolean {
        return country.lowercase(Locale.ROOT) == "argentina" || country.isNotEmpty()
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

    fun saveCompleteData(token: String, user: User, onError: () -> Unit, onSuccess: () -> Unit) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                // Activo el loading
                withContext(Dispatchers.Main) {
                    _isLoading.value = true
                    _error.value = ErrorBusco()
                }

                val response = repo.updateUser(token, user)

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

                // Desactivo el loading
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            // Desactivo el loading y manejo el error
            _isLoading.value = false
            _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")

        }
    }

}