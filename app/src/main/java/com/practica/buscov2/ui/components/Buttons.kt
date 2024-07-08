package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal


@Composable
fun ButtonPrincipal(modifier: Modifier = Modifier, text: String, enabled: Boolean, onSelected: () -> Unit) {
    Button(
        onClick = { onSelected() },
        modifier = modifier
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
fun ButtonGoogle(onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = { onClick() },
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
fun ButtonLine(text: String, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = { onClick() },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = text,
            color = GrayText,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
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


@Composable
fun ArrowBack(color: Color = OrangePrincipal) {
    Icon(
        painter = painterResource(id = R.drawable.arrow_back),
        contentDescription = "Volver",
        modifier = Modifier.size(60.dp),
        tint = color
    )
}

@Composable
fun ArrowSquareBack(onClick: () -> Unit) {
    IconButton(
        modifier = Modifier
            .height(56.dp)
            .border(1.dp, OrangePrincipal, RoundedCornerShape(12.dp)),
        onClick = { onClick() }) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Volver",
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            tint = OrangePrincipal
        )
    }

}

@Composable
fun ButtonMenu(modifier: Modifier, onClick: () -> Unit) {
    IconButton(
        modifier = modifier.background(color = OrangePrincipal, shape = CircleShape),
        onClick = { onClick() }) {
        Icon(
            painter = painterResource(id = R.drawable.burguer_menu),
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}

@Composable
fun ButtonBack(
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    navController: NavController,
    iconId: Int = R.drawable.arrow_back,
    onClick: () -> Unit = { navController.popBackStack() }
) {
    IconButton(
        modifier = modifier
            .size(size)
            .aspectRatio(1f)
            .shadow(10.dp, shape = CircleShape),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color.White,
            contentColor = OrangePrincipal
        ),
        onClick = { onClick() }) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "Volver",
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    iconId: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = OrangePrincipal
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "",
                tint = OrangePrincipal
            )
            Text(text = text, color = OrangePrincipal)
        }
    }
}


@Composable
fun ButtonClose(color: Color = Color.White, onClick: () -> Unit){
    IconButton(onClick = { onClick() }) {
        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = color)
    }
}