package com.practica.buscov2.ui.viewModel.professions

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.ProfessionsRepository
import com.practica.buscov2.model.busco.Profession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfessionsViewModel @Inject constructor(
    private val professionsRepo: ProfessionsRepository,
) : ViewModel() {

    private val _professions = mutableStateOf<List<Profession>>(emptyList())
    var professions: State<List<Profession>> = _professions

    private val _profession = mutableStateOf("")
    var profession: State<String> = _profession

    fun getProfessions(query: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    professionsRepo.getProfessionsSearch(query)
                }
                _professions.value = response ?: emptyList()
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }
    }

    fun getProfession(id:Int, onError: () -> Unit, onSuccess: (Profession) -> Unit){
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    professionsRepo.getProfession(id)
                }

                if (response != null) {
                    return@launch onSuccess(response)
                }

                onError()
            } catch (e: Exception) {
                onError()
                Log.e("Error", e.toString())
            }
        }
    }
}