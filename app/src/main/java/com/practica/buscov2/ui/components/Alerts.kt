package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.OrangePrincipal

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
fun AlertError(
    showDialog: MutableState<Boolean>,
    title: String,
    message: String,
    onClick: () -> Unit = { showDialog.value = false }
) {
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
                OutlinedButton(
                    onClick = { onClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "OK", color = Color.Black)
                }
            },
        )
    }
}

@Composable
fun AlertSuccess(
    showDialog: MutableState<Boolean>,
    text: String,
    onClick: () -> Unit = { showDialog.value = false }
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            title = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    InsertImage(
                        image = R.drawable.beeconfirmed, modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                    )
                }
            },
            text = {
                Text(
                    text = text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            },
            confirmButton = {
                ButtonPrincipal(
                    onSelected = { onClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    text = "OK"
                )
            },
        )
    }
}

@Composable
fun AlertSelectPicture(
    showDialog: MutableState<Boolean>,
    openCamera: () -> Unit,
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { openCamera() }, modifier = Modifier) {
                            InsertImage(R.drawable.camera, modifier = Modifier.fillMaxSize())
                        }
                        Text(text = "Tomar foto", fontSize = 16.sp)
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            InsertImage(R.drawable.gallery, modifier = Modifier.fillMaxSize())
                        }
                        Text(text = "Abrir galería", fontSize = 16.sp)
                    }
                }
            },
            confirmButton = { /*TODO*/ }
        )
    }
}

@Composable
fun AlertShowPicture(
    showDialog: MutableState<Boolean>,
    previousImage: String,
    actualImage: String,
    changePicture: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Cambiar foto de perfil")
                    Space(size = 5.dp)
                    InsertCircleProfileImage(
                        image = previousImage, modifier = Modifier
                            .size(160.dp)
                            .shadow(10.dp, shape = CircleShape)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.arrowmultiple),
                        contentDescription = "",
                        tint = OrangePrincipal,
                        modifier = Modifier.size(80.dp).padding(vertical = 15.dp),

                    )
                    InsertCircleProfileImage(
                        image = actualImage, modifier = Modifier
                            .size(160.dp)
                            .shadow(10.dp, shape = CircleShape)
                    )
                }
            },
            dismissButton = {
                ButtonLine(text = "Cancelar") {
                    showDialog.value = false
                }
            },
            confirmButton = {
                ButtonPrincipal(text = "Cambiar", enabled = true) {
                    changePicture()
                }
            }
        )
    }
}