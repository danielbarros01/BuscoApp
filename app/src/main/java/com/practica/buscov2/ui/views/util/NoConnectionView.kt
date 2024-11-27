package com.practica.buscov2.ui.views.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.theme.GrayText

@Composable
fun NoConnectionView(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        NoConnection(Modifier.align(Alignment.Center), navController)
    }
}

@Composable
fun NoConnection(modifier: Modifier, navController: NavHostController) {
    Column(
        modifier = modifier.padding(45.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(240.dp),
            painter = painterResource(id = R.drawable.noconnection),
            contentDescription = "Sin conexión",
        )
        Space(size = 24.dp)
        Text(text = "Sin conexión al servidor", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Space(size = 4.dp)
        Text(
            text = "Asegurate de estar conectado a la red, si el problema persiste, intentalo de nuevo más tarde.",
            color = GrayText,
            textAlign = TextAlign.Center
        )
        Space(size = 24.dp)
        ButtonPrincipal(
            text = "Intentarlo de nuevo",
            enabled = true
        ) {
            navController.navigate("Start")
        }
    }
}