package com.practica.buscov2.ui.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.practica.buscov2.data.pagination.JobsDataSource
import com.practica.buscov2.data.pagination.QualificationsDataSource
import com.practica.buscov2.data.repository.busco.QualificationsRepository
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.ResponseCreatedId
import com.practica.buscov2.model.busco.auth.ErrorBusco
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QualificationsViewModel @Inject constructor(
    private val repo: QualificationsRepository
) : ViewModel() {
    /*UI*/
    private val _buttonEnabled = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled
    /*UI*/

    var qualification by mutableStateOf(Qualification())
        private set

    private val _commentary: MutableState<String> = mutableStateOf("")
    var commentary: State<String> = _commentary

    private val _rating: MutableState<Float> = mutableStateOf(0f)
    var rating: State<Float> = _rating

    val _quantity: MutableState<Int> = mutableStateOf(0)
    var quantity: State<Int> = _quantity

    private val _filterStars: MutableState<Int?> = mutableStateOf(null)
    var filterStars: State<Int?> = _filterStars

    private val _ratingFrequencies = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val ratingFrequencies: StateFlow<Map<Int, Int>> = _ratingFrequencies

    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val qualificationsPage = _refreshTrigger
        .flatMapLatest {
            Pager(PagingConfig(pageSize = 6)) {
                QualificationsDataSource(
                    repo,
                    qualification.workerUserId ?: 0,
                    filterStars.value,
                    this
                )
            }.flow.cachedIn(viewModelScope)
        }


    fun createQualification(token: String, onError: (ErrorBusco) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                repo.createQualification(token, qualification)
            }

            when (response) {
                is Boolean -> {
                    if (response) {
                        onSuccess()
                    }
                }

                is ErrorBusco -> {
                    onError(response)
                }
            }
            //--
        }
    }

    fun refreshQualifications(){
        _refreshTrigger.value++
    }

    fun setCommentary(value: String) {
        _commentary.value = value
        qualification = qualification.copy(commentary = value)

        _buttonEnabled.value = enabledButton()
    }


    fun setRating(value: Float) {
        _rating.value = value
        qualification = qualification.copy(score = value)

        _buttonEnabled.value = enabledButton()
    }

    fun setWorkerId(value: Int) {
        qualification = qualification.copy(workerUserId = value)
    }

    fun updateAverageAndQuantity(average:Float, quantity:Int) {
        _quantity.value = quantity
        _rating.value = average
    }

    fun updateRatingFrequencies(frequencies: Map<Int, Int>?) {
        _ratingFrequencies.value = frequencies ?: emptyMap()
    }

    fun setFilterStars(value: String?) {
        _filterStars.value = value?.toIntOrNull()
    }

    private fun enabledButton(): Boolean {
        return (qualification.score != null && qualification.score!! in 1f..5f)
                && qualification.commentary?.isNotEmpty() == true
    }
}