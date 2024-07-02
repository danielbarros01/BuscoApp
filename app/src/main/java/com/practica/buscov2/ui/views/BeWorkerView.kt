package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.ButtonLine
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.Circle
import com.practica.buscov2.ui.components.InsertImage
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.YellowStar

@Composable
fun BeWorkerView(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        BeWorker(Modifier.align(Alignment.TopStart), navController)
    }
}

@Composable
fun BeWorker(modifier: Modifier, navController: NavController) {
    Column(modifier = modifier.padding(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        LogoBeWorker()
        Space(size = 15.dp)
        Title(text = "Registrate\ncomo trabajador", size = 30.sp)
        Space(size = 5.dp)
        Text(
            text = "Agrega tu profesión y empieza\na buscar trabajo.",
            textAlign = TextAlign.Center
        )

        Space(size = 15.dp)
        Circles()
        Space(size = 15.dp)

        ButtonLine("No, gracias") {
            navController.navigate("Home")
        }
        Space(size = 5.dp)
        ButtonPrincipal(text = "Ser trabajador", enabled = true) {
            navController.navigate("RegisterWorker")
        }
    }
}

@Composable
private fun Circles() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 60.dp)
    ) {

        Circle(
            modifier = Modifier.align(Alignment.TopStart),
            color = OrangePrincipal,
            size = 20.dp
        )
        Circle(modifier = Modifier.align(Alignment.BottomEnd), color = YellowStar, size = 14.dp)
    }
}

@Composable
private fun LogoBeWorker() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 20.dp)
    ) {

        InsertImage(
            image = R.drawable.job,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )

        InsertImage(
            image = R.drawable.flyingbee,
            modifier = Modifier
                .size(125.dp)
                .align(Alignment.TopStart)
                .graphicsLayer {
                    rotationZ = 180f
                }
                .offset(x = 0.dp, y = (20).dp)
        )
        InsertImage(
            image = R.drawable.flyingbee,
            modifier = Modifier
                .size(125.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer {
                    rotationZ = 270f
                }
        )
        InsertImage(
            image = R.drawable.flyingbee,
            modifier = Modifier
                .size(125.dp)
                .align(Alignment.BottomStart)
                .graphicsLayer {
                    rotationZ = 90f
                }
        )
        InsertImage(
            image = R.drawable.flyingbee,
            modifier = Modifier
                .size(125.dp)
                .align(Alignment.BottomEnd)
        )
    }
}




