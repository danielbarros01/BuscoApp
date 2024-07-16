package com.practica.buscov2.ui.viewModel.proposals

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProposalViewModel @Inject constructor(
    private val repo: ProposalsRepository
) : ViewModel() {
    /*UI*/
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error

    /*UI*/
    private val _proposal = mutableStateOf<Proposal?>(null)
    val proposal: State<Proposal?> = _proposal

    private val _userOwner = mutableStateOf<User?>(null)
    val userOwner: State<User?> = _userOwner

    fun getProposal(id: Int, onError: () -> Unit, onSuccess: (Proposal) -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = withContext(Dispatchers.IO) {
                    repo.getProposal(id)
                }

                if(response is Proposal){
                    _proposal.value = response
                    onSuccess(response)
                }

                //Si la respuesta es nula ocurrio un error
                if(response == null){
                    throw Exception()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changeUserOwner(user: User){
        _userOwner.value = user
    }
}