package com.practica.buscov2.ui.views.users

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.auth.ErrorBusco
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.georef.Pais
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ArrowBack
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.DateField
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.OnBoardNavButton
import com.practica.buscov2.ui.components.PagerIndicator
import com.practica.buscov2.ui.components.SearchField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.util.ConstantsDates.Companion.MAX_DATE_BIRTHDATE
import com.practica.buscov2.util.ConstantsDates.Companion.MIN_DATE_BIRTHDATE
import java.time.Instant
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompleteDataView(
    viewModel: CompleteDataViewModel,
    navController: NavController,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    vmSearch: SearchMapViewModel,
    username: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CompleteData(
            Modifier.align(Alignment.Center),
            viewModel,
            vmToken,
            vmLoading,
            vmSearch,
            navController,
            username
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteData(
    modifier: Modifier,
    viewModel: CompleteDataViewModel,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    vmSearch: SearchMapViewModel,
    navController: NavController,
    username: String
) {
    val user = viewModel.user

    val currentPage = remember { mutableIntStateOf(0) }
    val showError = remember { mutableStateOf(false) }
    val error = viewModel.error
    val isLoading by vmLoading.isLoading

    //Mostrar botones en el datePicker si la fecha es correcta o no hay nada
    val enabledButtonDate = remember { mutableStateOf(true) }

    // Para manejar la fecha seleccionada
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }
    val stateDataPicker = rememberDatePickerState()
    selectedDateMillis.value = stateDataPicker.selectedDateMillis

    //Configurar validaciones de fecha
    LaunchedEffect(stateDataPicker.selectedDateMillis) {
        stateDataPicker.selectedDateMillis?.let { dateMillis ->
            ConfigMinAndMaxDate(
                viewModel,
                dateMillis,
                selectedDateMillis,
                showError,
                enabledButtonDate
            )
        }
    }

    //----------
    Scaffold(
        // Barra inferior
        bottomBar = {
            BottomBarPart(
                currentPage,
                viewModel,
                user,
                vmToken,
                vmLoading,
                showError,
                navController
            )
        },

        // Contenido principal
        content = {
            AlertError(showDialog = showError, error.value.title, error.value.message)
            if (isLoading) {
                LoaderMaxSize()
            }

            //Si estoy en la segunda parte
            if (currentPage.intValue > 0) {
                IconButton(
                    modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                    onClick = {
                        currentPage.intValue--
                        viewModel.changeEnabledButton(true)
                    }) {
                    ArrowBack()
                }
            }

            Column(
                modifier = modifier
                    .padding(15.dp)
                    .padding(it)
            ) {
                InsertImage(
                    R.drawable.logo,
                    Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 15.dp, bottom = 10.dp)
                )

                TitleAndName(username)

                //Si es pagina 1
                if (currentPage.intValue == 0) {
                    PageOne(viewModel, stateDataPicker, showError, enabledButtonDate)
                    //PageTwo(vm = viewModel, vmSearch = vmSearch)
                } else if (currentPage.intValue == 1) {
                    //Segunda pagina
                    PageTwo(vm = viewModel, vmSearch = vmSearch)
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ConfigMinAndMaxDate(
    viewModel: CompleteDataViewModel,
    dateMillis: Long,
    selectedDateMillis: MutableState<Long?>,
    showError: MutableState<Boolean>,
    enabledButtonDate: MutableState<Boolean>
) {
    val minDateMillis =
        MIN_DATE_BIRTHDATE.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val maxDateMillis =
        MAX_DATE_BIRTHDATE.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    if (dateMillis < minDateMillis) {
        val adjustedDate = MIN_DATE_BIRTHDATE
        val adjustedMillis =
            adjustedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        selectedDateMillis.value = adjustedMillis

        viewModel.setDateOfBirth("${adjustedDate.dayOfMonth}/${adjustedDate.monthValue}/${adjustedDate.year}")

        showError.value = true
        enabledButtonDate.value = false

        viewModel.setError(
            ErrorBusco(
                title = "Fecha muy antigua",
                message = "La fecha no puede ser menor a 01/01/1950"
            )
        )
    } else if (dateMillis > maxDateMillis) {
        val adjustedDate = MAX_DATE_BIRTHDATE
        val adjustedMillis =
            adjustedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        selectedDateMillis.value = adjustedMillis

        viewModel.setDateOfBirth("${adjustedDate.dayOfMonth}/${adjustedDate.monthValue}/${adjustedDate.year}")

        showError.value = true
        enabledButtonDate.value = false

        viewModel.setError(
            ErrorBusco(
                title = "Fecha excedida",
                message = "Debes tener por lo menos 18 años"
            )
        )
    } else {
        val localDate =
            Instant.ofEpochMilli(dateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
        viewModel.setDateOfBirth("${localDate.dayOfMonth}/${localDate.monthValue}/${localDate.year}")
        showError.value = false
        enabledButtonDate.value = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageOne(
    viewModel: CompleteDataViewModel,
    stateDataPicker: DatePickerState,
    showError: MutableState<Boolean>,
    enabledButtonDate: MutableState<Boolean>
) {
    val name: String by viewModel.name
    val lastname: String by viewModel.lastname
    val dateOfBirth: String by viewModel.dateOfBirth
    val showDatePicker = remember { mutableStateOf(false) }

    Column {
        Column {
            Text(text = "Nombre", color = GrayText)
            CommonField(name, "Nombre") {
                viewModel.onDateChanged(it, lastname, dateOfBirth)
            }
        }
        Space(8.dp)
        Column {
            Text(text = "Apellido", color = GrayText)
            CommonField(lastname, "Apellido") {
                viewModel.onDateChanged(name, it, dateOfBirth)
            }
        }

        Space(8.dp)

        Column {
            Text(text = "Fecha de nacimiento", color = GrayText)
            DateField(
                text = dateOfBirth,
                R.drawable.calendar,
                "Seleccionar Fecha de nacimiento",
                "Fecha de nacimiento",
            ) {
                showDatePicker.value = true
            }
        }


        //Mostrar seleccionador de fecha
        if (showDatePicker.value) {
            ShowDatePicker(
                stateDataPicker,
                showDatePicker,
                showError,
                enabledButtonDate
            )
        }
        Space(8.dp)
    }
}

@Composable
fun PageTwo(
    vm: CompleteDataViewModel,
    vmSearch: SearchMapViewModel
) {
    var search by remember { mutableStateOf("") }
    val places by vmSearch.places
    var showMarker by remember {
        mutableStateOf(false)
    }
    val placeCoordinates by vmSearch.placeCoordinates

    val markerState = remember { mutableStateOf(MarkerState(position = placeCoordinates)) }
    val cameraPosition = remember { CameraPosition.fromLatLngZoom(placeCoordinates, 2f) }
    val cameraState = rememberCameraPositionState { position = cameraPosition }

    LaunchedEffect(placeCoordinates) {
        if (showMarker) {
            markerState.value = MarkerState(position = placeCoordinates)
            cameraState.animate(CameraUpdateFactory.newLatLngZoom(placeCoordinates, 8f))
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¿Dónde vives?", fontSize = 20.sp)
        SearchField(
            onQueryChange = {
                search = it

                if (search.isNotEmpty()) {
                    vmSearch.getLocation(search)
                }
            },
            onSearch = {
                if (search.isNotEmpty()) {
                    vmSearch.getLocation(search)
                }
            },
            content = {
                places.results.forEach { p ->
                    val lat = p.geometry.location.lat
                    val lng = p.geometry.location.lng
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                vmSearch.setLocation(lat, lng)
                                vm.changeUbication(lat, lng)
                                showMarker = true
                                it() //Close search
                            }
                            .padding(10.dp)
                    ) {
                        Text(text = p.formatted_address, fontSize = 18.sp)
                    }

                    HorizontalDivider(color = Color.Gray, thickness = 1.dp)

                }
            })
        Space(size = 8.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(shape = RoundedCornerShape(10.dp))
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraState
            ) {
                if (showMarker) Marker(state = markerState.value)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowDatePicker(
    stateDataPicker: DatePickerState,
    showDatePicker: MutableState<Boolean>,
    showError: MutableState<Boolean>,
    enabledButtonDate: MutableState<Boolean>
) {
    DatePickerDialog(
        onDismissRequest = {
            showDatePicker.value = false
            showError.value = false
        },
        confirmButton = {
            Button(
                onClick = {
                    showDatePicker.value = false
                    showError.value = false
                },
                enabled = enabledButtonDate.value
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    showDatePicker.value = false
                    showError.value = false
                },
            ) {
                Text(text = "Cancelar", color = GrayText)
            }
        }) {
        DatePicker(state = stateDataPicker)
    }
}

@Composable
private fun BottomBarPart(
    currentPage: MutableState<Int>,
    viewModel: CompleteDataViewModel,
    user: User,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    showError: MutableState<Boolean>,
    navController: NavController
) {
    val token by vmToken.token.collectAsState()
    val buttonEnable: Boolean by viewModel.buttonEnable
    val cantPage = 2

    Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 20.dp)) {
        PagerIndicator(
            size = cantPage,
            currentPage = currentPage.value
        )

        OnBoardNavButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            currentPage = currentPage.value,
            noOfPages = cantPage,
            enabled = buttonEnable, {
                currentPage.value++
                viewModel.changeEnabledButton(false)
            }
        ) {
            //Guardar los datos
            token?.let {
                vmLoading.withLoading {
                    viewModel.saveCompleteData(it.token, user, {
                        //En caso de error
                        showError.value = true
                    }) {
                        //En caso de exito navegar al home
                        navController.navigate("BeWorker")
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleAndName(name: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title(text = "Bienvenido", size = 30.sp, verticalPadding = 0.dp)
        Title(
            text = "@$name",
            size = 20.sp,
            color = OrangePrincipal,
            verticalPadding = 0.dp,
            modifier = Modifier.offset(y = -5.dp)
        )
        Text(
            text = "Antes de continuar, necesitamos que completes tu perfil.",
            color = GrayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}

