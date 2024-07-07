package com.practica.buscov2.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.AlertSuccess
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.InsertCirlceProfileEditImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.viewModel.workers.RegisterWorkerViewModel
import com.practica.buscov2.ui.views.users.ConfigMinAndMaxDate
import com.practica.buscov2.ui.views.users.PageOne
import com.practica.buscov2.ui.views.users.PageTwo
import com.practica.buscov2.ui.views.workers.DataWorker
import com.practica.buscov2.util.AppUtils.Companion.toLocalDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserView(
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmWorker: RegisterWorkerViewModel,
    vmCompleteData: CompleteDataViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    // Fetch user profile once
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {}
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
            initializeUserData(vmCompleteData, vmWorker, it)
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
    user: User
) {
    vmCompleteData.onDateChanged(
        user.name ?: "",
        user.lastname ?: "",
        user.birthdate ?: ""
    )

    vmCompleteData.onDateChangedInitializedData(user)

    user.worker?.let {
        vmWorker.onCategoryChangeForId(
            it.workersProfessions?.first()?.profession?.categoryId ?: 1
        )
        vmWorker.fetchProfessions {
            vmWorker.onProfessionChange(
                it.workersProfessions?.first()?.profession?.name ?: ""
            )
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
    navController: NavHostController,
    stateDataPicker: DatePickerState
) {
    // State variables
    val isLoading by vmWorker.isLoading
    val buttonEnabled by vmWorker.buttonEnabled
    val error = vmWorker.error
    val errorData = vmCompleteData.error
    val showError = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val dataWorker = remember { mutableStateOf(false) }
    val selectedDateMillis = remember { mutableStateOf<Long?>(null) }
    val enabledButtonDate = remember { mutableStateOf(true) }

    // Configure date picker limits
    LaunchedEffect(stateDataPicker.selectedDateMillis) {
        stateDataPicker.selectedDateMillis?.let { dateMillis ->
            ConfigMinAndMaxDate(
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
                            navController.navigate("Profile")
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
                image = user.image ?: "",
                modifier = Modifier
                    .size(160.dp)
                    .shadow(10.dp, shape = CircleShape),
                onClick = {}
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
                    PageTwo(vm = vmCompleteData)
                    SaveButton(
                        token,
                        vmCompleteData = vmCompleteData,
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
                    vmWorker.updateWorker(token.token, handleError, handleSuccess)
                }

                vmCompleteData != null -> {
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
