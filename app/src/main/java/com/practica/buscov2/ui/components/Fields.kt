package com.practica.buscov2.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.practica.buscov2.R

@Composable
fun CommonField(email: String, onTextFieldChanged: (String) -> Unit) {
    OutlinedTextField(
        value = email, onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .border(width = 2.dp, color = Color(0xFF565656), shape = RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        placeholder = { Text(text = "Ingrese su email/username") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color(0xFF565656)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(password: String, onTextFieldChanged: (String) -> Unit) {
    // Creating a variable to store toggle state
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { onTextFieldChanged(it) },
        placeholder = { Text(text = "Ingrese su contraseña") },
        modifier = Modifier
            .border(width = 2.dp, color = Color(0xFF565656), shape = RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color(0xFF565656)
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            val icon =
                if (passwordVisible) R.drawable.eyeview
                else R.drawable.eyeblock;

            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(painter = painterResource(id = icon), contentDescription = description)
            }
        }
    );
}