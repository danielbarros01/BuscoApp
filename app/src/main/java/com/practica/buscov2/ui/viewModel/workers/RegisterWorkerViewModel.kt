package com.practica.buscov2.ui.viewModel.workers

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.ProfessionsRepository
import com.practica.buscov2.data.repository.busco.WorkersRepository
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.ProfessionCategory
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterWorkerViewModel @Inject constructor(
    private val professionsRepo: ProfessionsRepository,
    private val workersRepository: WorkersRepository,
) : ViewModel() {
    private val _buttonEnabled = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled

    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error


    //datos
    var worker by mutableStateOf(Worker())
        private set

    private val _categories = mutableStateOf<List<ProfessionCategory>>(emptyList())
    var categories: State<List<ProfessionCategory>> = _categories
    private val _category = mutableStateOf("Seleccione un sector")
    var category: State<String> = _category

    private val _professions = mutableStateOf<List<Profession>>(emptyList())
    var professions: State<List<Profession>> = _professions
    private val _profession = mutableStateOf("Seleccione una profesión")
    var profession: State<String> = _profession

    private val _title = mutableStateOf("")
    var title: State<String> = _title

    private val _yearsExperience = mutableStateOf("")
    var yearsExperience: State<String> = _yearsExperience

    private val _description = mutableStateOf("")
    var description: State<String> = _description

    private val _webpage = mutableStateOf("")
    var webpage: State<String> = _webpage

    init {
        //traigo categorias
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    professionsRepo.getCategories()
                }

                _categories.value = response ?: emptyList()
            } catch (e: Exception) {
                _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")
                Log.e("Error", e.toString())
            }
        }
    }

    fun fetchProfessions(ok: () -> Unit = {}) {
        val categoryId = categories.value.find { it.name == category.value }?.id ?: 1

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    professionsRepo.getProfessions(categoryId)
                }
                _professions.value = response ?: emptyList()

                ok()
            } catch (e: Exception) {
                _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")
                Log.e("Error", e.toString())
            }
        }
    }

    fun updateWorker(token: String, onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    worker.let {
                        //Validar pagina web
                        addHttpsToWeb(worker)

                        //Actualizar
                        workersRepository.updateWorker(token, it)
                    }
                }

                when (response) {
                    is Boolean -> {
                        if (response) onSuccess()
                    }

                    is ErrorBusco -> {
                        _error.value = response
                        onError()
                    }
                }
            } catch (e: Exception) {
                _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")
                Log.e("Error", e.toString())
            }
        }
    }

    private fun addHttpsToWeb(worker: Worker) {
        if (worker.webPage != null) {
            if(!worker.webPage!!.startsWith("http://") && !worker.webPage!!.startsWith("https://")) {
                worker.webPage = "https://${worker.webPage}"
            }
        }
    }

    fun registerWorker(token: String, onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    worker.let {
                        workersRepository.createWorker(token, it)
                    }
                }

                when (response) {
                    is Boolean -> {
                        if (response) onSuccess()
                    }

                    is ErrorBusco -> {
                        _error.value = response
                        onError()
                    }
                }
            } catch (e: Exception) {
                _error.value = ErrorBusco(message = "Ha ocurrido un error inesperado")
                Log.e("Error", e.toString())
            }
        }
    }


    fun onCategoryChange(category: String) {
        if (isValidCategory(category)) {
            _category.value = category
            _profession.value = "Seleccione una profesión"
        }

        _buttonEnabled.value = enabledButton()
    }

    fun onCategoryChangeForId(categoryId: Int) {
        if (isValidCategoryForId(categoryId)) {
            _category.value = returnCategoryForId(categoryId).first().name.toString()
            _profession.value = "Seleccione una profesión"
        }

        _buttonEnabled.value = enabledButton()
    }

    fun onProfessionChange(profession: String) {
        if (isValidProfession(profession)) {
            val id = professions.value.find { it.name == profession }?.id
            val list = listOf(id)

            _profession.value = profession
            worker = worker.copy(professionsId = list)
        }

        _buttonEnabled.value = enabledButton()
    }

    private fun enabledButton(): Boolean {
        val isEnabled = isValidCategory(category.value) && isValidProfession(profession.value)
                && isValidWebPage(webpage.value) && isValidYears(yearsExperience.value)
                && isValidDescription(description.value)

        return isEnabled
    }

    //Que la categoria seleccionada este entre las categorias disponibles
    private fun isValidCategory(category: String): Boolean {
        return categories.value.map { it.name }.contains(category)
    }

    private fun isValidCategoryForId(categoryId: Int): Boolean {
        return categories.value.map { it.id }.contains(categoryId)
    }
    private fun returnCategoryForId(categoryId: Int): List<ProfessionCategory> {
        return categories.value.filter { it.id == categoryId }
    }

    //Que la profesion seleccionada este entre las disponibles
    private fun isValidProfession(profession: String): Boolean {
        return professions.value.map { it.name }.contains(profession)
    }

    private fun isValidWebPage(webpage: String): Boolean {
        return webpage.isEmpty() || Patterns.WEB_URL.matcher(webpage).matches()
    }

    private fun isValidYears(years: String): Boolean {
        return years.toIntOrNull() != null
    }

    private fun isValidDescription(description: String): Boolean {
        return description.length in 15..255
    }

    fun onWebpageChange(web: String) {
        _webpage.value = web
        worker = worker.copy(webPage = web)

        _buttonEnabled.value = enabledButton()
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
        worker = worker.copy(description = description)

        _buttonEnabled.value = enabledButton()
    }

    fun onYearsChange(years: String) {
        _yearsExperience.value = years
        worker = worker.copy(yearsExperience = years.toIntOrNull())

        _buttonEnabled.value = enabledButton()
    }

    fun onTitleChange(title: String) {
        _title.value = title
        worker = worker.copy(title = title)

        _buttonEnabled.value = enabledButton()
    }

}