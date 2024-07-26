package com.practica.buscov2.ui.components

import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.RedBusco

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
    openGallery: () -> Unit
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
                        IconButton(onClick = { openGallery() }) {
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
                        modifier = Modifier
                            .size(80.dp)
                            .padding(vertical = 15.dp),

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

@Composable
fun AlertVerificationDelete(
    showDialog: MutableState<Boolean>,
    title: String,
    message: String,
    onClick: () -> Unit
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
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Eliminar",
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
                Button(
                    onClick = { onClick() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedBusco,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            modifier = Modifier.border(1.dp, RedBusco, shape = AlertDialogDefaults.shape)
        )
    }
}

@Composable
fun AlertDifferentJob(
    showDialog: MutableState<Boolean>,
    proposal: Proposal,
    user: User?,
    onDismiss: () -> Unit = { showDialog.value = false },
    onClick: () -> Unit
) {
    val forJob = proposal.profession?.name ?: ""
    val youAre = user?.worker?.workersProfessions?.first()?.profession?.name ?: ""

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.alertcircle),
                    contentDescription = "",
                    tint = OrangePrincipal,
                    modifier = Modifier.size(90.dp)
                )
            },
            title = {
                BasicText(
                    buildAnnotatedString {
                        append("Esta propuesta es para un ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = OrangePrincipal
                            )
                        ) {
                            append(forJob)
                        }
                        append(", pero tú eres un ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = OrangePrincipal
                            )
                        ) {
                            append(youAre)
                        }
                        append(". ¿Aún así deseas aplicar?")
                    },
                    style = TextStyle(fontSize = 24.sp, textAlign = TextAlign.Center)
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ButtonLine(text = "No") {
                            onDismiss()
                        }
                    }
                    Space(size = 5.dp)
                    Box(modifier = Modifier.weight(1f)) {
                        ButtonPrincipal(text = "Si", enabled = true) {
                            onClick()
                        }
                    }
                }
            })
    }
}


@Composable
fun AlertFinishProposal(
    showDialog: MutableState<Boolean>,
    onDismiss: () -> Unit = { showDialog.value = false },
    onClick: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.alertcircle),
                    contentDescription = "",
                    tint = GreenBusco,
                    modifier = Modifier.size(90.dp)
                )
            },
            title = {
                Text(
                    text = "Una vez concluida la propuesta, se considera terminado tanto el trabajo " +
                            "como la relación del trabajador asignado con esta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ButtonLine(text = "Cancelar") {
                            onDismiss()
                        }
                    }
                    Space(size = 5.dp)
                    Box(modifier = Modifier.weight(1f)) {
                        ButtonPrincipal(text = "Finalizar", enabled = true, color = GreenBusco) {
                            onClick()
                        }
                    }
                }
            })

    }
}


@Composable
fun AlertQualify(
    showDialog: MutableState<Boolean>,
    name: String,
    rating: Float,
    commentary: String,
    buttonEnabled: Boolean = true,
    changeCommentary: (String) -> Unit,
    onStars: (Float) -> Unit,
    onDismiss: () -> Unit = { showDialog.value = false },
    onClick: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Title(text = "Calificar a $name")
                    Space(size = 5.dp)
                    StarRatingBar(5, rating) {
                        onStars(it)
                    }
                    Space(size = 5.dp)

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Deja un comentario", fontSize = 16.sp)
                        CommonFieldArea(commentary, "", textStyle = TextStyle(fontSize = 14.sp)) {
                            changeCommentary(it)
                        }
                    }
                }
            },
            onDismissRequest = { },
            confirmButton = {
                ButtonPrincipal(text = "Calificar", enabled = buttonEnabled, color = GreenBusco) {
                    onClick()
                }
            },
            dismissButton = {
                ButtonLine(text = "No, gracias") {
                    onDismiss()
                }
            })
    }
}

