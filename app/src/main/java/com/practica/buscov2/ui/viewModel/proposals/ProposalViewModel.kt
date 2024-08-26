package com.practica.buscov2.ui.viewModel.proposals

import android.content.Context
import android.net.Uri
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
import com.practica.buscov2.util.FilesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun editProposal(
        context: Context,
        uri: Uri,
        token: String,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                var uriModified: Uri? = uri

                /**Esto es porque si tiene http, la imagen no se modifico
                 * Es la que ya venia del servidor, por eso no hay que volver a enviarla y
                 * el valor es nulo*/
                if (uri.toString().contains("http")) {
                    uriModified = null
                }

                val filePart =
                    if (!uriModified?.scheme.isNullOrEmpty() && !uriModified?.path.isNullOrEmpty()) {
                        FilesUtils.getFilePart(context, uri, "image")
                    } else {
                        null
                    }

                val response = withContext(Dispatchers.IO) {
                    proposal.value?.let { repo.editProposal(it, filePart, token) }
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