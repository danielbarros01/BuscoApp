package com.practica.buscov2.ui.views.confirmation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.ButtonTransparent
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.confirmation.CheckEmailViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun CheckEmailView(
    vm: CheckEmailViewModel,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController,
    user: User,
    forView: String = "check-email" //or recover-password
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CheckEmail(
            Modifier.align(Alignment.Center),
            vm,
            vmToken,
            vmLoading,
            navController,
            user,
            forView
        )
    }
}

@Composable
fun CheckEmail(
    modifier: Modifier,
    vm: CheckEmailViewModel,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController,
    user: User,
    forView: String
) {
    //Si es para checkEmail debo reenviar el codigo, si es para recuperar la contraseña no(uso sendCode)
    val resend = forView.equals("check-email", ignoreCase = true)

    val token by vmToken.token.collectAsState()

    val error = vm.error
    val showError = remember { mutableStateOf(false) }
    val isLoading: Boolean by vmLoading.isLoading
    val navigate = remember { mutableStateOf(false) }

    var digit1 by remember { mutableStateOf("") }
    var digit2 by remember { mutableStateOf("") }
    var digit3 by remember { mutableStateOf("") }
    var digit4 by remember { mutableStateOf("") }

    val enabledPrincipal: Boolean by vm.verificarEnabled

    var enabledSendCode by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        vm.startTimer(vm.waitingTimeMillis)
    }

    val remainingTime by vm.remainingTime.collectAsState()
    val totalSeconds = remainingTime?.let { TimeUnit.MILLISECONDS.toSeconds(it).toInt() } ?: 0
    enabledSendCode = totalSeconds == 0

    if (showError.value) {
        AlertError(showDialog = showError, error.title, error.message)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    if (navigate.value) {
        navController.navigate("Home")
    }

    Column(modifier = modifier.padding(15.dp)) {
        InsertImage(
            R.drawable.emailverification,
            Modifier
                .width(300.dp)
                .height(300.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )
        Space(size = 10.dp)
        Title(text = "Verifica tu correo", textAlign = TextAlign.Start)
        Space(2.dp)
        Text(text = "Introduce los 4 digitos que enviamos a ${user.email}")
        Space(size = 15.dp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            BoxNumber(digit1) {
                digit1 = it
                vm.addDigit(
                    n1 = if (it.isNotEmpty()) it.toInt() else null,
                    n2 = if (digit2.isNotEmpty()) digit2.toInt() else null,
                    n3 = if (digit3.isNotEmpty()) digit3.toInt() else null,
                    n4 = if (digit4.isNotEmpty()) digit4.toInt() else null
                )
            }
            BoxNumber(digit2) {
                digit2 = it
                vm.addDigit(
                    n1 = if (digit1.isNotEmpty()) digit1.toInt() else null,
                    n2 = if (it.isNotEmpty()) it.toInt() else null,
                    n3 = if (digit3.isNotEmpty()) digit3.toInt() else null,
                    n4 = if (digit4.isNotEmpty()) digit4.toInt() else null
                )
            }
            BoxNumber(digit3) {
                digit3 = it
                vm.addDigit(
                    n1 = if (digit1.isNotEmpty()) digit1.toInt() else null,
                    n2 = if (digit2.isNotEmpty()) digit2.toInt() else null,
                    n3 = if (it.isNotEmpty()) it.toInt() else null,
                    n4 = if (digit4.isNotEmpty()) digit4.toInt() else null
                )
            }
            BoxNumber(digit4) {
                digit4 = it
                vm.addDigit(
                    n1 = if (digit1.isNotEmpty()) digit1.toInt() else null,
                    n2 = if (digit2.isNotEmpty()) digit2.toInt() else null,
                    n3 = if (digit3.isNotEmpty()) digit3.toInt() else null,
                    n4 = if (it.isNotEmpty()) it.toInt() else null
                )
            }
        }
        Space(size = 10.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Space(size = 5.dp)

            Text(text = if (remainingTime != null) formatTime(remainingTime!!) else "00:00")

            Space(size = 5.dp)

            ButtonTransparent(text = "Reenviar código", enabled = enabledSendCode) {
                vmLoading.withLoading {
                    vm.resendCode(
                        resend,
                        token = token?.token,
                        email = user.email ?: "",
                        onError = {
                            showError.value = true
                        }) {
                        vm.startTimer(vm.waitingTimeMillis)
                        enabledSendCode = false
                    }
                }
            }
            Space(size = 5.dp)
            ButtonPrincipal(text = "Verificar", enabled = enabledPrincipal) {
                vmLoading.withLoading {
                    vm.validateCode(
                        resend = resend,
                        token = token?.token ?: "",
                        code = vm.digitsString,
                        email = user.email ?: "",
                        {
                            showError.value = true
                        }) {
                        if (resend) {
                            navController.navigate("CompleteData/${user.username}")
                        } else {
                            navController.navigate("ResetPassword")
                        }
                    }
                }
            }
            Space(size = 5.dp)
            ButtonTransparent(
                text = "Cancelar",
                textDecoration = TextDecoration.Underline
            ) {
                navController.navigate("Login") {
                    // Pop up to the start destination and remove it from the back stack
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    // Evitar múltiples instancias de la actividad actual
                    launchSingleTop = true
                }
            }
        }

    }
}


@Composable
fun formatTime(timeMi: Long): String {
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMi).toInt()

    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}

@Composable
private fun BoxNumber(number: String = "", onChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .border(
                BorderStroke(width = 2.dp, color = GrayPlaceholder), // Optional border
                shape = RoundedCornerShape(20.dp)
            )
            .width(70.dp)
            .height(70.dp),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp),
            value = number,
            onValueChange = {
                if (it.length <= 1) {
                    onChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = GrayText, fontSize = 36.sp, textAlign = TextAlign.Center),
            singleLine = true,
        )
    }
}


















