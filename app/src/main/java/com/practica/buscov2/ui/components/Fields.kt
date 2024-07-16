package com.practica.buscov2.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText

@Composable
fun CommonField(
    text: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextFieldChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = text, onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .border(width = 1.dp, color = GrayField, shape = RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        placeholder = { Text(text = placeholder, color = GrayPlaceholder) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = GrayText,
            focusedTextColor = Color.Black
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
        placeholder = { Text(text = "Ingrese su contraseña", color = GrayPlaceholder) },
        modifier = Modifier
            .border(width = 1.dp, color = GrayField, shape = RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
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

@Composable
fun DateField(
    text: String,
    icon: Int,
    iconDescription: String,
    placeholder: String,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = { },
        placeholder = { Text(text = placeholder, color = GrayPlaceholder) },
        modifier = Modifier
            .border(width = 1.dp, color = GrayField, shape = RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .clickable { onClick() },
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            //unfocusedTextColor = GrayPlaceholder
            disabledTextColor = GrayText,
            disabledPlaceholderColor = GrayPlaceholder,
            disabledTrailingIconColor = GrayText
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            IconButton(onClick = { onClick() }) {
                Icon(painter = painterResource(id = icon), contentDescription = iconDescription)
            }
        },
        enabled = false
    );
}

@Composable
fun CommonFieldArea(
    text: String,
    placeholder: String,
    onTextFieldChanged: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    OutlinedTextField(
        value = text, onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier
            .border(width = 1.dp, color = GrayField, shape = RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(104.dp)
            .verticalScroll(scrollState),
        placeholder = { Text(text = placeholder, color = GrayPlaceholder) },
        maxLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = GrayText,
            focusedTextColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsField(
    options: List<String>,
    text: String,
    enabled: Boolean = true,
    onChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.padding()
    ) {
        TextField(
            // The `menuAnchor` modifier must be passed to the text field to handle
            // expanding/collapsing the menu on click. A read-only text field has
            // the anchor type `PrimaryNotEditable`.
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .border(width = 1.dp, color = GrayField, shape = RoundedCornerShape(8.dp))
                .fillMaxWidth(),
            value = text,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = GrayText,
                unfocusedTextColor = GrayText,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onChanged(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

