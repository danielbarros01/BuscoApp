package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal


@Composable
fun ButtonPrincipal(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    color: Color? = OrangePrincipal,
    shape: Shape = RoundedCornerShape(12.dp),
    onSelected: () -> Unit
) {
    Button(
        onClick = { onSelected() },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color!!,
            contentColor = Color.White
        ),
        shape = shape,
        enabled = enabled,
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
    fontSize: TextUnit = 18.sp,
    text: String,
    enabled: Boolean = true,
    textDecoration: TextDecoration? = null,
    onSelected: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { onSelected() },
        enabled = enabled
    ) {
        Text(
            text = text,
            color = if (enabled) OrangePrincipal else GrayPlaceholder,
            fontSize = fontSize,
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
fun ButtonSquareSmall(
    modifier: Modifier = Modifier,
    color: Color = OrangePrincipal,
    iconId: Int = R.drawable.arrow_back,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .height(56.dp)
            .border(1.dp, color, RoundedCornerShape(12.dp)),
        onClick = { onClick() }) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "Volver",
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            tint = color
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
    fontSize: TextUnit = 18.sp,
    iconSize: Dp = 22.dp,
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
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "",
                tint = OrangePrincipal,
                modifier = Modifier.size(iconSize)
            )
            Space(size = 1.dp)
            Text(text = text, color = OrangePrincipal, fontSize = fontSize)
        }
    }
}


@Composable
fun ButtonClose(color: Color = Color.White, onClick: () -> Unit) {
    IconButton(onClick = { onClick() }) {
        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = color)
    }
}

@Composable
fun ButtonUbication(
    address: String,
    modifier: Modifier = Modifier,
    textColor: Color = GrayField,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = { onClick() }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
        ),
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "",
                tint = textColor,
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 5.dp)
            )
            Text(
                text = address,
                color = textColor,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 18.sp,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = if (enabled) OrangePrincipal else GrayPlaceholder,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = OrangePrincipal),
            ) {
                onClick()
            }
    )
}