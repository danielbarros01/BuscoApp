package com.practica.buscov2.ui.viewModel.proposals

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.practica.buscov2.data.pagination.ApplicationsDataSource
import com.practica.buscov2.data.repository.busco.ApplicationsRepository
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val repo: ApplicationsRepository
) : ViewModel() {
    /*UI*/
    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error
    /*UI*/

    private val _proposalId: MutableState<Int?> = mutableStateOf(null)
    private val proposalId = _proposalId

    private val _token = MutableStateFlow<String?>(null)
    private val token = _token

    private val _applicant = mutableStateOf<Application?>(null)
    val applicant: State<Application?> = _applicant

    private val _status = mutableStateOf<Boolean?>(true)
    val status: State<Boolean?> = _status

    private val _refreshTrigger = MutableStateFlow(0)

    //Traer postulantes
    @OptIn(ExperimentalCoroutinesApi::class)
    val applicantsPage =
        _refreshTrigger.flatMapLatest {
            Pager(PagingConfig(pageSize = 4)) {
                ApplicationsDataSource(repo, proposalId.value!!, tokenP = token.value!!)
            }.flow.cachedIn(viewModelScope)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val applicationsPage =
        _refreshTrigger.flatMapLatest {
            Pager(PagingConfig(pageSize = 4)) {
                ApplicationsDataSource(
                    repo,
                    tokenP = token.value!!,
                    myApplicationsP = true,
                    statusP = status.value
                )
            }.flow.cachedIn(viewModelScope)
        }


    fun acceptOrDeclineApplication(
        proposalId: Int,
        applicationId: Int,
        status: Boolean,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repo.acceptOrDeclineApplication(
                        token.value!!,
                        proposalId,
                        applicationId,
                        status
                    )
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
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError()
            }
        }
    }

    fun getAcceptedApplication(proposalId: Int, onSuccess: (Worker?) -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repo.getApplicationAccepted(proposalId)
            }

            when (response) {
                is Application -> {
                    _applicant.value = response
                    onSuccess(response.worker)
                }
            }
        }
    }

    fun applyToProposal(proposalId: Int, onError: (ErrorBusco) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    repo.createApplication(token.value!!, proposalId)
                }

                when (response) {
                    is Boolean -> {
                        if (response) {
                            onSuccess()
                        }
                    }

                    is ErrorBusco -> {
                        _error.value = response
                        onError(response)
                    }
                }
            } catch (e: Exception) {
                _error.value = ErrorBusco(
                    title = "Error",
                    message = "Ha ocurrido un error inesperado, intentalo de nuevo más tarde"
                )
                onError(error.value)
            }
        }
    }

    fun refreshProposals() {
        _refreshTrigger.value++
    }

    fun setToken(token: String) {
        _token.value = token
    }

    fun setProposalId(id: Int) {
        _proposalId.value = id
    }

    fun changeStatus(status: Boolean?) {
        _status.value = status
    }
}