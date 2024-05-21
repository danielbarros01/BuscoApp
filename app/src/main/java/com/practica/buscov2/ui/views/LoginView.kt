package com.practica.buscov2.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ButtonGoogle
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.Logo
import com.practica.buscov2.ui.components.PasswordField
import com.practica.buscov2.ui.components.SeparatoryLine
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.viewModel.LoginViewModel

@Composable
fun LoginView(viewModel: LoginViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Login(Modifier.align(Alignment.Center), viewModel)
    }
}

@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel) {
    val email: String by viewModel.email
    val password: String by viewModel.password
    val loginEnable: Boolean by viewModel.loginEnabled
    val showError = remember { mutableStateOf(false) }
    val error = viewModel.error

    if (showError.value) {
        AlertError(showDialog = showError, error.title, error.message)
    }

    Column(modifier = modifier) {
        Logo(
            Modifier
                .width(250.dp)
                .height(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )
        Title("Bienvenido!")
        Space(8.dp)
        CommonField(email) { viewModel.onLoginChanged(it, password) }
        Space(8.dp)
        PasswordField(password) { viewModel.onLoginChanged(email, it) }
        Space(4.dp)
        ForgotPassword()
        Space(16.dp)

        ButtonPrincipal("Iniciar Sesión", loginEnable) {
            viewModel.login(onError = { showError.value = true }) {}
        }

        Space(12.dp)
        NoAccount()
        Space(4.dp)
        SeparatoryLine()
        Space(4.dp)
        ButtonGoogle()
    }

}

@Composable
fun NoAccount() {
    Row(modifier = Modifier.padding(start = 6.dp)) {
        Text(
            text = "No tienes una cuenta?",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF565656)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = "Registrate",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2F89FC),
            modifier = Modifier.clickable { }
        )
    }
}

@Composable
fun ForgotPassword() {
    Text(
        text = "Olvide mi contraseña",
        modifier = Modifier.clickable { },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2F89FC)
    )
}



