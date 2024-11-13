package com.practica.buscov2.ui.viewModel.proposals

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProposalViewModel @Inject constructor(
    private val repo: ProposalsRepository,
) : ViewModel() {
    /*UI*/
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
                val response = withContext(Dispatchers.IO) {
                    repo.getProposal(id)
                }

                if (response is Proposal) {
                    _proposal.value = response
                    onSuccess(response)
                }

                //Si la respuesta es nula ocurrio un error
                if (response == null) {
                    throw Exception()
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                Log.e("Error", e.toString())
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError()
            }
        }
    }

    fun deleteProposal(id: Int, token: String, onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repo.deleteProposal(id, token)
                }

                when (response) {
                    is Boolean -> {
                        if (response) {
                            onSuccess()
                        }
                    }

                    is ErrorBusco -> {
                        _error.value = response
                        onError()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError()
            }
        }
    }


    fun finalizeProposal(id: Int, token: String, onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repo.finalizeProposal(token, id)
                }

                when (response) {
                    is Boolean -> {
                        if (response) {
                            onSuccess()
                        }
                    }

                    is ErrorBusco -> {
                        _error.value = response
                        onError()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError()
            }
        }
    }

    fun changeProposal(proposal: Proposal) {
        _proposal.value = proposal
    }

    fun changeUserOwner(user: User) {
        _userOwner.value = user
    }

    fun setError(error: ErrorBusco) {
        _error.value = error
    }
}