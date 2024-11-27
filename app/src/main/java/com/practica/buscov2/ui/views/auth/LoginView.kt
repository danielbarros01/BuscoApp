package com.practica.buscov2.ui.views.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.practica.buscov2.R
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
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.LoginViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel

@Composable
fun LoginView(
    viewModel: LoginViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Login(
            Modifier.align(Alignment.Center),
            viewModel,
            vmUser,
            vmGoogle,
            vmLoading,
            navController
        )
    }
}

@Composable
fun NoAccount(onClick: () -> Unit) {
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
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun HaveAccount(onClick: () -> Unit) {
    Row(modifier = Modifier.padding(start = 6.dp)) {
        Text(
            text = "Ya tienes una cuenta?",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF565656)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = "Iniciar Sesión",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2F89FC),
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun ForgotPassword(onClick: () -> Unit) {
    Text(
        text = "Olvide mi contraseña",
        modifier = Modifier.clickable { onClick() },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2F89FC)
    )
}

@Composable
fun Login(
    modifier: Modifier,
    viewModel: LoginViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val loginValue: String by viewModel.loginValue
    val password: String by viewModel.password
    val loginEnable: Boolean by viewModel.loginEnabled
    val showError = remember { mutableStateOf(false) }
    val error = viewModel.error
    val isLoading: Boolean by vmLoading.isLoading

    //val user by vmUser.user.collectAsState()
    if (showError.value) {
        AlertError(showDialog = showError, error.title, error.message)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    Column(modifier = modifier.padding(15.dp)) {
        InsertImage(
            R.drawable.logo,
            Modifier
                .width(250.dp)
                .height(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )
        Space(2.dp)
        Title(text = "Bienvenido!")
        Space(25.dp)
        CommonField(
            loginValue,
            "Ingrese su email/username",
            KeyboardType.Email
        ) { viewModel.onLoginChanged(it, password) }
        Space(8.dp)
        PasswordField(password) { viewModel.onLoginChanged(loginValue, it) }
        Space(4.dp)
        ForgotPassword {
            navController.navigate("RecoverPassword")
        }
        Space(16.dp)

        ButtonPrincipal(text = "Iniciar Sesión", enabled = loginEnable) {
            vmLoading.withLoading {
                viewModel.login(onError = { showError.value = true }) { token ->
                    vmUser.getMyProfile(token, {}) {
                        //En caso de obtener el usuario
                        //Si esta confirmado
                        if (it.confirmed == true) {
                            //Si los datos estan completados, ir a Home, si no, a completar los datos
                            if (it.name != null && it.lastname != null) {
                                navController.navigate("Home")
                            } else {
                                navController.navigate("CompleteData/${it.username}")
                            }
                        }
                        //Si no esta confirmado
                        else {
                            val userJson = Gson().toJson(it)
                            navController.navigate("CheckEmailView/$userJson/check-email")
                        }
                    }
                }
            }
        }

        Space(12.dp)
        NoAccount {
            navController.navigate("RegisterView")
        }
        Space(4.dp)
        SeparatoryLine()
        Space(4.dp)
        GoogleLogin(vmGoogle, viewModel, vmUser, vmLoading, navController) {
            //En caso de error
            showError.value = true
        }
    }
}

@Composable
fun GoogleLogin(
    vmGoogle: GoogleLoginViewModel,
    vmLogin: LoginViewModel,
    vmUser: UserViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController,
    onError: () -> Unit
) {
    vmGoogle.initialize(LocalContext.current)

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(intent)

                    //Iniciar sesion
                    vmLoading.withLoading {
                        vmLogin.loginWithGoogle(task, onError = { onError() }) { token ->
                            //Obtener el usuario
                            vmUser.getMyProfile(token, {}) {
                                //Si los datos estan completados, ir a Home
                                if (it.name != null && it.lastname != null) {
                                    navController.navigate("Home")
                                }
                                //Si no estan completados los datos, ir a CompleteData
                                else {
                                    navController.navigate("CompleteData/${it.username}")
                                }
                            }
                        }
                    }

                }
            }
        }

    ButtonGoogle(onClick = { startForResult.launch(vmGoogle.getSignInIntent()) })
}





