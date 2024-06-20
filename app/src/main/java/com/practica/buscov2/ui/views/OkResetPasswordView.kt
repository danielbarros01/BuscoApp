package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title

@Composable
fun OkResetPassword(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        OkReset(Modifier, navController)
    }
}

@Composable
fun OkReset(modifier: Modifier, navController: NavController) {
    Column(modifier = modifier.padding(15.dp)) {
        Space(size = 30.dp)
        
        InsertImage(
            image = R.drawable.beeconfirmed, modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )
        Space(size = 10.dp)

        Title(
            text = "Éxito",
            size = 50.sp,
        )
        Space(size = 5.dp)

        Text(
            text = "Su contraseña fue restablecida",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Space(size = 30.dp)

        ButtonPrincipal(text = "Continuar", enabled = true) {
            navController.navigate("Start")
        }
    }
}
