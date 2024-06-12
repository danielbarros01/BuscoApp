package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.StartViewModel
import com.practica.buscov2.ui.viewModel.TokenViewModel
import com.practica.buscov2.ui.viewModel.UserViewModel
import com.practica.buscov2.util.AppUtils

//Decidira a donde debemos ir

@Composable
fun StartView(
    vm: StartViewModel,
    vmToken: TokenViewModel,
    vmUser: UserViewModel,
    navController: NavController
) {
    val token by vmToken.token.collectAsState()

    LaunchedEffect(Unit) {
        if (token != null) {
            //verificar que el token no este vencido
            if (AppUtils.expiredDate(token!!.expiration)) { //!! en vez de let ya que anteriormente verificamos que token no sea null
                vmToken.deleteToken()
                navController.navigate("Login")
            } else {
                //Verificar que este confirmado el usuario
                vmUser.getMyProfile(token!!.token, {
                    //En caso de error(ej.No existe usuario) ir al Login
                    navController.navigate("Login")
                }) { currentUser ->
                    //En caso de obtener el usuario
                    //Si esta confirmado
                    if (currentUser.confirmed == true) {
                        //Si los datos estan completados, ir a Home, si no, a completar los datos
                        if (currentUser.name != null && currentUser.lastname != null) {
                            navController.navigate("Home")
                        } else {
                            navController.navigate("CompleteData/${currentUser.username}")
                        }
                    }
                    //Si no esta confirmado
                    else {
                        val userJson = Gson().toJson(currentUser)
                        navController.navigate("CheckEmailView/${userJson}")
                    }
                }
            }
        } else {
            navController.navigate("Login")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        StartPage(Modifier.align(Alignment.Center))
    }
}

@Composable
fun StartPage(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        InsertImage(
            R.drawable.logo,
            Modifier
                .width(200.dp)
                .height(200.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )
        Space(size = 20.dp)
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.CenterHorizontally),
            color = OrangePrincipal,
            trackColor = GrayPlaceholder,
        )
    }
}