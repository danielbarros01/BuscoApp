package com.practica.buscov2.ui.views.users

import com.practica.buscov2.ui.views.images.SelectImageFromGallery
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.data.dataStore.StoreUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.AlertSelectPicture
import com.practica.buscov2.ui.components.AlertShowPicture
import com.practica.buscov2.ui.components.AlertSuccess
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.ClickableText
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.InsertCirlceProfileEditImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.ubication.MapViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.viewModel.workers.RegisterWorkerViewModel
import com.practica.buscov2.ui.views.images.UseCamera
import com.practica.buscov2.ui.views.maps.MapViewUI
import com.practica.buscov2.ui.views.workers.DataWorker
import com.practica.buscov2.util.AppUtils.Companion.toLocalDate
import kotlinx.coroutines.launch
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserView(
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmWorker: RegisterWorkerViewModel,
    vmCompleteData: CompleteDataViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    // Fetch user profile once
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {user ->
                user.latitude?.let { latitude ->
                    user.longitude?.let { longitude ->
                        searchMapVM.getLocation(latitude, longitude) { address ->
                            address?.let {results ->
                                searchMapVM.setAddress(results.formatted_address)
                            }
                        }

                        vmCompleteData.changeUbication(latitude, longitude)
                    }
                }
            }
        }
    }

    user?.let {
        // Initialize state variables
        val initialDate = it.birthdate?.toLocalDate()
        val initialDateMillis =
            initialDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        val stateDataPicker =
            rememberDatePickerState(initialDateMillis ?: System.currentTimeMillis())

        // Initialize user and worker data once
        LaunchedEffect(Unit) {
            initializeUserData(vmCompleteData, vmWorker, vmLoading, it)
        }

        // Render EditUser screen
        Box(modifier = Modifier.fillMaxSize()) {
            EditUser(
                Modifier.align(Alignment.Center),
                vmUser,
                it,
                vmWorker,
                vmCompleteData,
                token,
                vmLoading,
                searchMapVM,
                mapVM,
                navController,
                stateDataPicker
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun initializeUserData(
    vmCompleteData: CompleteDataViewModel,
    vmWorker: RegisterWorkerViewModel,
    vmLoading: LoadingViewModel,
    user: User
) {
    vmCompleteData.onDateChanged(
        user.name ?: "",
        user.lastname ?: "",
        user.birthdate ?: ""
    )

    user.worker?.let {
        vmWorker.onCategoryChangeForId(
            it.professions?.first()?.categoryId ?: 1
        )

        vmLoading.withLoading {
            vmWorker.fetchProfessions {
                vmWorker.onProfessionChange(
                    it.professions?.first()?.name ?: ""
                )
            }
        }

        vmWorker.onTitleChange(it.title ?: "")
        vmWorker.onYearsChange(it.yearsExperience?.toString() ?: "")
        vmWorker.onDescriptionChange(it.description ?: "")
        vmWorker.onWebpageChange(it.webPage ?: "")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUser(
    modifier: Modifier,
    vmUser: UserViewModel,
    user: User,
    vmWorker: RegisterWorkerViewModel,
    vmCompleteData: CompleteDataViewModel,
    token: LoginToken?,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController,
    stateDataPicker: DatePickerState
) {
    val context = LocalContext.current
    // State variables
    val isLoading by vmLoading.isLoading
    val error = vmWorker.error
    val errorData = vmCompleteData.error
    val scope = rememberCoroutineScope()

    val showError = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val dataWorker = remember { mutableStateOf(false) }
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }
    val enabledButtonDate = remember { mutableStateOf(true) }

    val openAlertSelectImage = remember { mutableStateOf(false) }
    val openCamera = remember { mutableStateOf(false) }
    val openGallery = remember { mutableStateOf(false) }
    val openAlertChangePicture = remember { mutableStateOf(false) }

    val picture = remember {
        mutableStateOf(user.image ?: "")
    }
    val newUriPicture = remember {
        mutableStateOf(Uri.EMPTY)
    }

    val changeUbication = remember {
        mutableStateOf(false)
    }
    val ubicationDataStore = StoreUbication(context)


    if (changeUbication.value) {
        MapViewUI(
            user.latitude!!,
            user.longitude!!,
            searchMapVM,
            mapVM,
            navController,
            actionClose = {
                changeUbication.value = false //Close Map
            }) {
            //Cambiar ubicacion en la memoria del celular
            scope.launch {
                ubicationDataStore.saveUbication(it)
                searchMapVM.setLocation(it.latitude, it.longitude)
                vmCompleteData.changeUbication(it.latitude, it.longitude)
                changeUbication.value = false //close map
            }
        }
    }

    val address = searchMapVM.address.value

    // Configure date picker limits
    LaunchedEffect(stateDataPicker.selectedDateMillis) {
        stateDataPicker.selectedDateMillis?.let { dateMillis ->
            configMinAndMaxDate(
                viewModel = vmCompleteData,
                dateMillis = dateMillis,
                selectedDateMillis = selectedDateMillis,
                showError = showError,
                enabledButtonDate = enabledButtonDate
            )
        }
    }

    // UI Components
    if (isLoading) LoaderMaxSize()
    AlertError(showDialog = showError, error.value.title, error.value.message)
    AlertError(showDialog = showError, errorData.value.title, errorData.value.message)
    AlertSuccess(showDialog = showDialog, "Datos guardados con éxito")
    AlertSelectPicture(
        showDialog = openAlertSelectImage,
        openCamera = {
            openCamera.value = true
            openAlertSelectImage.value = false
        },
        openGallery = {
            openGallery.value = true
            openAlertSelectImage.value = false
        }
    )

    AlertShowPicture(openAlertChangePicture, picture.value, newUriPicture.value.toString()) {
        //Que hacer en caso de que se acepte
        //Cambiar en el editProfile
        picture.value = newUriPicture.value.toString()

        //Guardar
        if (token != null) {
            vmUser.updatePictureProfile(
                context = context,
                newUriPicture.value,
                token.token,
                onError = {
                    showError.value = true
                },
                onSuccess = {
                    showDialog.value = true
                }
            )
        }

        //Salir
        openAlertChangePicture.value = false
    }

    if (openCamera.value) {
        UseCamera(onBack = { openCamera.value = false }) { uri ->
            //La logica cuando tengo la foto
            //salgo de la camara
            openCamera.value = false
            newUriPicture.value = uri
            openAlertChangePicture.value = true
        }
    }

    if (openGallery.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        ) {
            SelectImageFromGallery(onGalleryClosed = {
                openGallery.value = false
            }) { uri ->
                newUriPicture.value = uri
                openAlertChangePicture.value = true
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 5.dp),
                title = { Text(text = "") },
                navigationIcon = {
                    ButtonBack(
                        navController = navController,
                        size = 48.dp,
                        iconId = R.drawable.close,
                        modifier = Modifier.padding(start = 10.dp),
                        onClick = {
                            navController.navigate("Profile/${user.id}")
                        }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            InsertCirlceProfileEditImage(
                image = picture.value,
                modifier = Modifier
                    .size(160.dp)
                    .shadow(10.dp, shape = CircleShape),
                onClick = {
                    openAlertSelectImage.value = true
                }
            )

            // Toggle between user and worker data
            if (user.worker != null) {
                Space(size = 5.dp)

                Button(onClick = { dataWorker.value = !dataWorker.value }) {
                    Text(text = if (dataWorker.value) "Ver datos de usuario" else "Ver Datos de Trabajador")
                }

                if (dataWorker.value) {
                    DataWorker(vmWorker = vmWorker)
                    SaveButton(
                        token,
                        vmWorker = vmWorker,
                        vmLoading = vmLoading,
                        showDialog = showDialog,
                        showError = showError
                    )
                } else {
                    PageOne(
                        viewModel = vmCompleteData,
                        stateDataPicker = stateDataPicker,
                        showError = showError,
                        enabledButtonDate = enabledButtonDate
                    )

                    //Ubicacion
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Ubicación", color = GrayText)
                            ClickableText(text = "Cambiar", fontSize = 14.sp, enabled = true) {
                                changeUbication.value = true
                            }
                        }
                        CommonField(address, "Ubicación") {

                        }
                    }

                    //PageTwo(vm = vmCompleteData)
                    SaveButton(
                        token,
                        vmCompleteData = vmCompleteData,
                        vmLoading = vmLoading,
                        showDialog = showDialog,
                        showError = showError
                    )
                }
            }
        }
    }
}

@Composable
fun SaveButton(
    token: LoginToken?,
    vmWorker: RegisterWorkerViewModel? = null,
    vmCompleteData: CompleteDataViewModel? = null,
    vmLoading: LoadingViewModel,
    showDialog: MutableState<Boolean>,
    showError: MutableState<Boolean>
) {
    val buttonEnabled = vmWorker?.buttonEnabled ?: vmCompleteData?.buttonEnable

    ButtonPrincipal(
        modifier = Modifier.padding(vertical = 15.dp),
        text = "Guardar",
        enabled = buttonEnabled!!.value
    ) {
        token?.let { token ->
            val handleError: () -> Unit = {
                showError.value = true
            }
            val handleSuccess: () -> Unit = {
                showDialog.value = true
            }

            when {
                vmWorker != null -> {
                    vmLoading.withLoading {
                        vmWorker.updateWorker(token.token, handleError, handleSuccess)
                    }
                }

                vmCompleteData != null -> {
                    vmLoading.withLoading {
                        vmCompleteData.saveCompleteData(
                            token = token.token,
                            onError = handleError,
                            onSuccess = handleSuccess
                        )
                    }
                }
            }
        }
    }
}
