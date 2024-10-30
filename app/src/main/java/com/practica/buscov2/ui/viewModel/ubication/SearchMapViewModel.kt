package com.practica.buscov2.ui.viewModel.ubication

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.practica.buscov2.BuildConfig
import com.practica.buscov2.model.maps.MapResult
import com.practica.buscov2.model.maps.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SearchMapViewModel @Inject constructor() : ViewModel() {
    var lat by mutableDoubleStateOf(-31.37023520656901)
        private set

    var long by mutableDoubleStateOf(-64.23292894961696)
        private set

    private val _placeCoordinates = mutableStateOf(LatLng(lat, long))
    var placeCoordinates: State<LatLng> = _placeCoordinates

    private val _address = mutableStateOf("")
    var address: State<String> = _address

    var show by mutableStateOf(false)
        private set

    private val _places = mutableStateOf<MapResult>(MapResult(results = listOf()))
    var places: State<MapResult> = _places

    fun getLocation(value: String) {
        viewModelScope.launch {
            val apiKey = BuildConfig.API_KEY_MAPS
            val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$value&key=$apiKey"

            val response = withContext(Dispatchers.IO) {
                URL(url).readText()
            }

            val results = Gson().fromJson(response, MapResult::class.java)
            _places.value = results
        }
    }

    fun getLocation(lat: Double, lng: Double, callback: (Results?) -> Unit) {
        viewModelScope.launch {
            val apiKey = BuildConfig.API_KEY_MAPS
            val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=$apiKey"
            val result = withContext(Dispatchers.IO) {
                val response = URL(url).readText()
                val results = Gson().fromJson(response, MapResult::class.java)
                results.results.getOrNull(0)
            }
            callback(result)
        }
    }

    fun setLocation(lat: Double, lng: Double) {
        this.lat = lat
        this.long = lng
        _placeCoordinates.value = LatLng(lat, lng)
        show = false
    }

    fun setAddress(value: String) {
        _address.value = value
    }
}