package com.practica.buscov2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R

@Preview(showBackground = true)
@Composable
fun AlertErrorPreview() {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.info_circle),
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Error al autenticarse")
            }
        },
        text = {
            Text(
                text = "Email o contrasena incorrecto. Por favor intente de nuevo.",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "OK", color = Color.Black)
            }
        },
    )
}

@Composable
fun AlertError(showDialog: MutableState<Boolean>, title: String, message: String) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.info_circle),
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = title, Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            },
            text = { Text(text = message) },
            confirmButton = {
                OutlinedButton(onClick = { showDialog.value = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "OK", color = Color.Black)
                }
            },
        )
    }
}