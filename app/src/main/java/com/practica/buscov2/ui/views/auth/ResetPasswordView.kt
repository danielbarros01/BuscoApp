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
import androidx.compose.runtime.collectAsState
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
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.PasswordField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.ResetPasswordViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel

@Composable
fun ResetPassword(
    vmReset: ResetPasswordViewModel,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Reset(Modifier.align(Alignment.Center), vmReset, vmToken, vmLoading, navController)
    }
}

@Composable
fun Reset(
    modifier: Modifier,
    vmReset: ResetPasswordViewModel,
    vmToken: TokenViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val token by vmToken.token.collectAsState()

    val password by vmReset.password
    val repeatPassword by vmReset.repeatPassword
    val buttonEnabled by vmReset.buttonEnabled
    val isLoading by vmLoading.isLoading
    val error = vmReset.error
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
            R.drawable.honeycombsecuritytwo,
            Modifier
                .width(275.dp)
                .height(275.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )

        Space(size = 20.dp)

        Title(
            text = "Restablece tu contraseña",
            size = 36.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold
        )
        Space(size = 5.dp)
        Text(text = "Ingresa la nueva contraseña y repitela por seguridad.")
        Space(size = 10.dp)
        PasswordField(password = password) {
            vmReset.onPasswordChanged(it, repeatPassword)
        }
        Space(size = 10.dp)
        PasswordField(password = repeatPassword) {
            vmReset.onPasswordChanged(password, it)
        }

        Space(size = 20.dp)
        ButtonPrincipal(text = "Cambiar contraseña", enabled = buttonEnabled) {
            token?.let {
                vmLoading.withLoading {
                    vmReset.changePassword(it.token, {
                        showError.value = true
                    }) {
                        navController.navigate("OkResetPassword")
                    }
                }
            }
        }
    }
}
