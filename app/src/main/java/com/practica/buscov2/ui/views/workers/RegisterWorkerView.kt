package com.practica.buscov2.ui.views.workers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.ProfessionCategory
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ArrowSquareBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.CommonFieldArea
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.OptionsField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.workers.RegisterWorkerViewModel

@Composable
fun RegisterWorkerView(
    vmWorker: RegisterWorkerViewModel,
    vmToken: TokenViewModel,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        RegisterWorker(Modifier.align(Alignment.Center), vmWorker, vmToken, navController)
    }
}

@Composable
fun RegisterWorker(
    modifier: Modifier,
    vmWorker: RegisterWorkerViewModel,
    vmToken: TokenViewModel,
    navController: NavController
) {
    val token by vmToken.token.collectAsState()
    val isLoading by vmWorker.isLoading
    val buttonEnabled by vmWorker.buttonEnabled
    val error = vmWorker.error
    val showError = remember { mutableStateOf(false) }

    //Mostrar error
    AlertError(showDialog = showError, error.value.title, error.value.message)

    if (isLoading) {
        LoaderMaxSize()
    }

    Column {
        BackgroundWelcome()
        Column(
            modifier = modifier.padding(
                start = 15.dp,
                end = 15.dp,
                top = 10.dp,
                bottom = 15.dp
            )
        ) {
            Text(text = "Cuentanos un poco más sobre tí", fontSize = 18.sp)
            Space(size = 6.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Space(size = 6.dp)
                WorkSector(vmWorker)
                Space(size = 6.dp)
                WorkProfession(vmWorker)
                Space(size = 6.dp)
                WorkTitle(vmWorker)
                Space(size = 6.dp)
                WorkYears(vmWorker)
                Space(size = 6.dp)
                WorkDescription(vmWorker)
                Space(size = 6.dp)
                WorkPage(vmWorker)
            }
            Space(size = 8.dp)

            Row() {
                ArrowSquareBack{
                    navController.navigate("Start")
                }
                Space(size = 5.dp)
                ButtonPrincipal(text = "Continuar", enabled = buttonEnabled) {
                    token?.let{
                        //Guardar datos de trabajador
                        vmWorker.registerWorker(it.token, {
                            //En caso de error
                            showError.value = true
                        }){
                            //Exito
                            navController.navigate("Home")
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun WorkPage(vmWorker: RegisterWorkerViewModel) {
    val webpage by vmWorker.webpage

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Página web", color = GrayText)
            Text(text = "Opcional", color = GrayPlaceholder)
        }
        CommonField(text = webpage, placeholder = "www.example.com") {
            if (it.length <= 45) vmWorker.onWebpageChange(it)
        }
    }
}

@Composable
fun WorkDescription(vmWorker: RegisterWorkerViewModel) {
    val description by vmWorker.description

    Column {
        Text(text = "Por qué deberian elegirte?", color = GrayText)
        CommonFieldArea(
            text = description,
            placeholder = "Describe tus habilidades y experiencia."
        ) {
            if (it.length <= 255) vmWorker.onDescriptionChange(it)
        }
    }
}

@Composable
fun WorkYears(vmWorker: RegisterWorkerViewModel) {
    val years by vmWorker.yearsExperience

    Column {
        Text(text = "Años de experiencia", color = GrayText)
        CommonField(text = years, placeholder = "ej. 5 ", keyboardType = KeyboardType.Number) {
            if (it.length <= 2) vmWorker.onYearsChange(it)
        }
    }
}

@Composable
fun WorkTitle(vmWorker: RegisterWorkerViewModel) {
    val title by vmWorker.title

    Column {
        Text(text = "Titula tu trabajo", color = GrayText)
        CommonField(text = title, placeholder = "ej. especializado en Pino/Roble ") {
            if (it.length <= 45) vmWorker.onTitleChange(it)
        }
    }
}

@Composable
fun WorkProfession(vmWorker: RegisterWorkerViewModel) {
    val professions by vmWorker.professions
    val professionSelected by vmWorker.profession

    Column {
        Text(text = "Cúal es tu profesión?", color = GrayText)
        OptionsField(professions.map { it.name }, professionSelected, true) {
            vmWorker.onProfessionChange(it)
        }
    }
}

@Composable
fun WorkSector(vmWorker: RegisterWorkerViewModel) {
    val categories by vmWorker.categories
    val categorySelected by vmWorker.category

    Column {
        Text(text = "En que sector trabajas?", color = GrayText)
        OptionsField(categories.map { it.name }, categorySelected, true) {
            vmWorker.onCategoryChange(it)
            vmWorker.fetchProfessions()
        }
    }
}

@Composable
fun BackgroundWelcome() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .paint(
                painter = painterResource(id = R.drawable.backgroundbeworker),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "B",
                        fontSize = 50.sp,
                        color = OrangePrincipal,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ienvenido",
                        fontSize = 46.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Text(
                    text = "Trabajador",
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

            }

        }
    }
}