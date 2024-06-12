package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practica.buscov2.R
import com.practica.buscov2.model.User
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.ButtonGoogle
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.CommonField
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.PasswordField
import com.practica.buscov2.ui.components.SeparatoryLine
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.viewModel.RegisterViewModel

@Composable
fun RegisterView(vm: RegisterViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Register(Modifier.align(Alignment.Center), vm, navController)
    }
}

@Composable
fun Register(modifier: Modifier, viewModel: RegisterViewModel, navController: NavController) {
    val username: String by viewModel.username
    val email: String by viewModel.email
    val password: String by viewModel.password
    val repeatedPassword: String by viewModel.repeatedPassword

    val buttonEnabled: Boolean by viewModel.buttonEnabled
    val showError = remember { mutableStateOf(false) }
    val error = viewModel.error
    val isLoading: Boolean by viewModel.isLoading


    if (showError.value) {
        AlertError(showDialog = showError, error.title, error.message)
    }

    if(isLoading){
        LoaderMaxSize()
    }

    Column(modifier = modifier.padding(15.dp)) {
        InsertImage(
            R.drawable.logo,
            Modifier
                .width(150.dp)
                .height(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )
        Space(size = 5.dp)
        Title(text = "Registrarse")
        Space(size = 10.dp)

        Column {
            Space(8.dp)
            CommonField(
                username,
                "Ingrese un nombre de usuario"
            ) {
                viewModel.onRegisterChanged(it, email, password, repeatedPassword)
            }
            Space(10.dp)
            CommonField(
                email,
                "Ingrese su email"
            ) {
                viewModel.onRegisterChanged(username, it, password, repeatedPassword)
            }
            Space(10.dp)
            PasswordField(
                password
            ) {
                viewModel.onRegisterChanged(username, email, it, repeatedPassword)
            }
            Space(10.dp)
            PasswordField(
                repeatedPassword
            ) {
                viewModel.onRegisterChanged(username, email, password, it)
            }
            Space(10.dp)

            ButtonPrincipal("Registrarse", buttonEnabled) {
                viewModel.register(onError = { showError.value = true }) {
                    val userJson = Gson().toJson(User(email = email, username = username))
                    navController.navigate("CheckEmailView/$userJson")
                }
            }
        }


        Space(12.dp)
        HaveAccount {
            navController.navigate("Login")
        }
        Space(4.dp)
        SeparatoryLine()
        Space(4.dp)
        ButtonGoogle()
    }
}
