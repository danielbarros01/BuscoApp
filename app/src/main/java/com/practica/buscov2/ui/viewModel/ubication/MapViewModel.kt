package com.practica.buscov2.ui.viewModel.ubication

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _enabledButton = mutableStateOf(false)
    var enabledButton: State<Boolean> = _enabledButton

    private val _showBottomSheet = mutableStateOf(false)
    var showBottomSheet: State<Boolean> = _showBottomSheet

    private val _address = mutableStateOf("")
    var address: State<String> = _address

    fun setEnabledButton(value: Boolean) {
        _enabledButton.value = value
    }

    fun setShowBottomSheet(value: Boolean) {
        _showBottomSheet.value = value
    }

    fun setAddress(value: String) {
        _address.value = value
    }
}