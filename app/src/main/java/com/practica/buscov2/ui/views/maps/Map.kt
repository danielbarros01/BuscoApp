package com.practica.buscov2.ui.views.maps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.practica.buscov2.model.maps.MapResult
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.SearchField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.viewModel.ubication.MapViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapViewUI(
    lat: Double,
    lng: Double,
    searchMapViewModel: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavController,
    actionClose: () -> Unit,
    action: (LatLng) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showBottomSheet by remember { mapVM.showBottomSheet }
    val address = searchMapViewModel.address.value
    val enabledButton = mapVM.enabledButton.value

    val placeCoordinates by searchMapViewModel.placeCoordinates

    // Componente principal que contiene el mapa y la barra de búsqueda
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
    ) {
        MapContent(lat, lng, searchMapViewModel)
        SearchBar(
            searchMapViewModel = searchMapViewModel,
            mapVM = mapVM,
            modifier = Modifier.align(Alignment.TopCenter),
            actionClose = actionClose,
            navController = navController
        )
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { mapVM.setShowBottomSheet(false) },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = address,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Space(size = 10.dp)
                    ButtonPrincipal(text = "Elegir", enabled = enabledButton) {
                        //Se puede cambiar el  enable dsi no se cambio la ubicacion todavia
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                action(placeCoordinates)
                                mapVM.setShowBottomSheet(false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapContent(
    lat: Double,
    lng: Double,
    searchMapViewModel: SearchMapViewModel
) {
    val placeCoordinates by searchMapViewModel.placeCoordinates

    // Recordar estados para el marcador y la cámara
    val markerState = remember { MarkerState(position = placeCoordinates) }
    val cameraState = rememberCameraPositionState()

    // Inicializar ubicación inicial
    InitializeLocation(lat, lng, searchMapViewModel)

    // Actualizar marcador y cámara cuando cambien las coordenadas
    UpdateMarkerAndCamera(placeCoordinates, markerState, cameraState)

    // Mapa principal
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState
    ) {
        Marker(state = markerState)
    }
}

@Composable
fun InitializeLocation(
    lat: Double,
    lng: Double,
    searchMapViewModel: SearchMapViewModel
) {
    // Actualiza la ubicación inicial y la dirección
    LaunchedEffect(lat, lng) {
        searchMapViewModel.setLocation(lat, lng)
        searchMapViewModel.getLocation(lat, lng) { result ->
            result?.formatted_address?.let {
                searchMapViewModel.setAddress(it)
            }
        }
    }
}

@Composable
fun UpdateMarkerAndCamera(
    placeCoordinates: LatLng,
    markerState: MarkerState,
    cameraState: CameraPositionState
) {
    // Efecto que se activa cuando cambian las coordenadas del lugar
    LaunchedEffect(placeCoordinates) {
        markerState.position = placeCoordinates
        cameraState.animate(CameraUpdateFactory.newLatLngZoom(placeCoordinates, 8f))
    }
}

@Composable
fun SearchBar(
    searchMapViewModel: SearchMapViewModel,
    mapVM: MapViewModel,
    modifier: Modifier = Modifier,
    actionClose: () -> Unit,
    navController: NavController
) {
    var search = searchMapViewModel.address.value
    val places by searchMapViewModel.places

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 15.dp)
    ) {
        ButtonBack(
            modifier = Modifier.padding(top = 9.dp),
            size = 50.dp,
            navController = navController
        ) { actionClose() }
        Space(5.dp)
        Box(modifier = Modifier.heightIn(max = 300.dp)) {
            SearchField(
                value = search,
                color = Color.White,
                onQueryChange = {
                    search = it
                    if (search.isNotEmpty()) {
                        searchMapViewModel.getLocation(search)
                    }
                },
                onSearch = {
                    if (search.isNotEmpty()) {
                        searchMapViewModel.getLocation(search)
                    }
                }
            ) {
                SearchResultsList(places) { latitude, longitude, address ->
                    // Actualiza las coordenadas cuando se selecciona un lugar
                    searchMapViewModel.setLocation(latitude, longitude)
                    searchMapViewModel.setAddress(address)
                    //Abrir dialogo para cambiar la ubicacion
                    mapVM.setShowBottomSheet(true)
                    mapVM.setEnabledButton(true)
                    it()
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(
    places: MapResult,
    onPlaceSelected: (Double, Double, String) -> Unit
) {
    places.results.forEach { place ->
        val latitude = place.geometry.location.lat
        val longitude = place.geometry.location.lng
        val address = place.formatted_address
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onPlaceSelected(latitude, longitude, address)
                }
                .padding(10.dp)
        ) {
            Text(text = address, fontSize = 18.sp)
        }
        HorizontalDivider(color = Color.Gray, thickness = 1.dp)
    }
}

@Composable
fun Map(
    lat: Double,
    lng: Double,
    searchMapViewModel: SearchMapViewModel,
    navController: NavController
) {
    var search by remember { mutableStateOf("") }
    val places by searchMapViewModel.places
    val placeCoordinates by searchMapViewModel.placeCoordinates

    // Usamos remember para mantener el estado del marcador
    val markerState = remember { MarkerState(position = placeCoordinates) }
    val cameraState = rememberCameraPositionState()

    // Actualiza la dirección inicial
    LaunchedEffect(lat, lng) {
        searchMapViewModel.setLocation(lat, lng)
        searchMapViewModel.getLocation(lat, lng) { result ->
            search = result?.formatted_address ?: ""
        }
    }

    // Efecto que se activa cuando cambian las coordenadas del lugar
    LaunchedEffect(placeCoordinates) {
        markerState.position = placeCoordinates
        cameraState.animate(CameraUpdateFactory.newLatLngZoom(placeCoordinates, 8f))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraState
        ) {
            Marker(state = markerState)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 15.dp)
                .align(Alignment.TopCenter)
        ) {
            ButtonBack(
                modifier = Modifier.padding(top = 9.dp),
                size = 50.dp,
                navController = navController
            )
            Space(5.dp)
            Box(modifier = Modifier.heightIn(max = 300.dp)) {
                SearchField(value = search, color = Color.White, onQueryChange = {
                    search = it
                    if (search.isNotEmpty()) {
                        searchMapViewModel.getLocation(search)
                    }
                }, onSearch = {
                    if (search.isNotEmpty()) {
                        searchMapViewModel.getLocation(search)
                    }
                }) {
                    places.results.forEach { p ->
                        val latitude = p.geometry.location.lat
                        val longitude = p.geometry.location.lng
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Actualiza las coordenadas del marcador y la cámara
                                    searchMapViewModel.setLocation(latitude, longitude)
                                    it()
                                }
                                .padding(10.dp)
                        ) {
                            Text(text = p.formatted_address, fontSize = 18.sp)
                        }

                        HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

