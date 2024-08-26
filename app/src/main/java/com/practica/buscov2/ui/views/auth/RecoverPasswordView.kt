package com.practica.buscov2.ui.views.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.RecoverPasswordViewModel

@Composable
fun RecoverPassword(
    vmRecover: RecoverPasswordViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Recover(Modifier.align(Alignment.Center), vmRecover, vmLoading, navController)
    }
}

@Composable
fun Recover(
    modifier: Modifier,
    vmRecover: RecoverPasswordViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val email by vmRecover.email
    val buttonEnabled by vmRecover.buttonEnabled
    val isLoading by vmLoading.isLoading
    val error = vmRecover.error
    val showError = remember {
        mutableStateOf(false)
    }

    if (showError.value) {
        AlertError(showDialog = showError, error.title, error.message)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    ButtonBack(
        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
        size = 64.dp,
        navController = navController
    ) {
        navController.navigate("Login")
    }

    Column(modifier = modifier.padding(15.dp)) {
        InsertImage(
            R.drawable.honeycombsecurity,
            Modifier
                .width(275.dp)
                .height(275.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )

        Space(size = 20.dp)

        Title(
            text = "Olvidaste tu contraseña?",
            size = 36.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold
        )
        Space(size = 10.dp)
        Text(text = "No te preocupes!  Ingresa el correo electronico asociado con tu cuenta")
        Space(size = 10.dp)
        CommonField(text = email, placeholder = "Ingrese su email") {
            vmRecover.onEmailChange(it)
        }
        Space(size = 20.dp)
        ButtonPrincipal(text = "Enviar", enabled = buttonEnabled) {
            vmLoading.withLoading {
                vmRecover.sendCode({
                    //En caso de error
                    showError.value = true
                }) {
                    val userJson = Gson().toJson(User(email = email))
                    //En caso de exito
                    navController.navigate("CheckEmailView/$userJson/recover-password")
                }
            }
        }
    }
}












