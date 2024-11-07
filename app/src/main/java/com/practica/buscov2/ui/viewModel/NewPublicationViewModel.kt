package com.practica.buscov2.ui.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.repository.busco.ProfessionsRepository
import com.practica.buscov2.data.repository.busco.ProposalsRepository
import com.practica.buscov2.model.busco.Profession
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.ResponseCreatedId
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.util.AppUtils.Companion.isGreaterThan
import com.practica.buscov2.util.Constants.Companion.ESQ_HOST
import com.practica.buscov2.util.FilesUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewPublicationViewModel @Inject constructor(
    private val proposalsRepository: ProposalsRepository,
) : ViewModel() {
    //UI
    private val _buttonEnabled = mutableStateOf(false)
    val buttonEnabled: State<Boolean> = _buttonEnabled

    private var _error = mutableStateOf(ErrorBusco())
    var error: State<ErrorBusco> = _error
    //UI

    //DATA
    var proposal by mutableStateOf(Proposal())
        private set

    private val _profession: MutableState<Profession?> = mutableStateOf(null)
    var profession: State<Profession?> = _profession

    private val _title: MutableState<String> = mutableStateOf("")
    var title: State<String> = _title

    private val _description: MutableState<String> = mutableStateOf("")
    var description: State<String> = _description

    private val _requirements: MutableState<String> = mutableStateOf("")
    var requirements: State<String> = _requirements

    private val _priceStart: MutableState<String> = mutableStateOf("")
    var priceStart: State<String> = _priceStart

    private val _priceUntil: MutableState<String> = mutableStateOf("")
    var priceUntil: State<String> = _priceUntil

    private val _image: MutableState<Uri> = mutableStateOf(Uri.EMPTY)
    var image: State<Uri> = _image

    private val _ubication = mutableStateOf<LatLng?>(null)
    var ubication: State<LatLng?> = _ubication

    fun setData(title: String, description: String, requirements: String) {
        _title.value = title
        _description.value = description
        _requirements.value = requirements

        proposal =
            proposal.copy(title = title, description = description, requirements = requirements)

        _buttonEnabled.value = enabledButton()
    }

    fun setId(id: Int) {
        proposal = proposal.copy(id = id)
    }

    fun setBudget(from: String, to: String) {
        // Establecer límites
        val maxLimit = 1000000

        // Intentar convertir 'from' a Double y manejar posibles excepciones
       val fromValue = try {
            from.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            null
        } ?: 0.0

        // Intentar convertir 'to' a Double y manejar posibles excepciones
        val toValue = try {
            to.toDoubleOrNull()
        } catch (e: NumberFormatException) {
            null
        } ?: 0.0

        _priceStart.value = if (fromValue > maxLimit) maxLimit.toString() else from
        _priceUntil.value = if (toValue > maxLimit) maxLimit.toString() else to

        proposal = proposal.copy(minBudget = fromValue, maxBudget = toValue)

        _buttonEnabled.value = enabledButton()
    }

    fun setProfession(profession: Profession?) {
        _profession.value = profession
        proposal = proposal.copy(professionId = profession?.id)
        _buttonEnabled.value = enabledButton()
    }

    fun setImage(uri: Uri?) {
        if(uri == null) return

        val fullImageUrl = "$ESQ_HOST${uri.path}"
        _image.value = Uri.parse(fullImageUrl)
        _buttonEnabled.value = enabledButton()
    }

    fun setUbication(lat: Double, lng: Double) {
        _ubication.value = LatLng(lat, lng)

        proposal = proposal.copy(
            latitude = lat,
            longitude = lng
        )
    }

    private fun enabledButton(): Boolean {
        //Validar precios min y max
        val isPriceValid = isGreaterThan(priceStart.value, priceUntil.value)
        val isDataValid = title.value.isNotEmpty()
                && (description.value.isNotEmpty() && description.value.length >= 20)
                && (requirements.value.isNotEmpty() && requirements.value.length >= 20)
                && profession.value != null

        return isPriceValid && isDataValid
    }

    fun createProposal(
        context: Context,
        uri: Uri,
        token: String,
        onError: () -> Unit,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val filePart = if (!uri.scheme.isNullOrEmpty() && !uri.path.isNullOrEmpty()) {
                    FilesUtils.getFilePart(context, uri, "image")
                } else {
                    null
                }

                val response = withContext(Dispatchers.IO) {
                    proposal.let {
                        proposalsRepository.createProposal(token, it, filePart)
                    }
                }

                when (response) {
                    is ResponseCreatedId -> {
                       onSuccess(response.id)
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
                    proposal.let { proposalsRepository.editProposal(it, filePart, token) }
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
}