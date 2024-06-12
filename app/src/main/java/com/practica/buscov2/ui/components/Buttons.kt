package com.practica.buscov2.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.OrangePrincipal


@Composable
fun ButtonPrincipal(text: String, enabled: Boolean, onSelected: () -> Unit) {
    Button(
        onClick = { onSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6422),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled
    )
    {
        Text(text = text)
    }
}

@Composable
fun ButtonGoogle() {
    OutlinedButton(
        onClick = { /*TODO*/ },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Icon(
                painter = painterResource(id = R.drawable.logogoogle),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = "Iniciar sesión con Google",
                color = Color(0xFF565656),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OnBoardNavButton(
    modifier: Modifier = Modifier,
    currentPage: Int,
    noOfPages: Int,
    enabled: Boolean = false,
    onNextClicked: () -> Unit,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            if (currentPage < noOfPages - 1) {
                onNextClicked()
            } else {
                onClick()
            }
        }, modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled
    ) {
        Text(text = if (currentPage < noOfPages - 1) "Continuar" else "Guardar")
    }
}

@Composable
fun ButtonTransparent(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    textDecoration: TextDecoration? = null,
    onSelected: () -> Unit
) {
    TextButton(onClick = { onSelected() }, enabled = enabled) {
        Text(
            text = text,
            color = if (enabled) OrangePrincipal else GrayPlaceholder,
            fontSize = 18.sp,
            style = TextStyle(textDecoration = textDecoration),
            fontWeight = FontWeight.Bold
        )
    }
}