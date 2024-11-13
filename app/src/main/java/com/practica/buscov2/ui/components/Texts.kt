package com.practica.buscov2.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.ui.theme.BlueLink
import com.practica.buscov2.ui.theme.GrayText

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 24.sp,
    color: Color = GrayText,
    verticalPadding: Dp = 15.dp,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = text,
        fontWeight = fontWeight,
        fontSize = size,
        modifier = modifier
            .fillMaxWidth(),
        textAlign = textAlign,
        color = color,
        style = TextStyle(lineHeight = 30.sp)
    )
}

@Composable
fun LinkText(link: String) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = AnnotatedString.Builder().apply {
        pushStyle(
            style = SpanStyle(
                color = BlueLink,
                fontWeight = FontWeight.SemiBold
            )
        )
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://$link",
        )
        append(link)
        pop()
    }.toAnnotatedString()

    Text(
        text = annotatedString,
        modifier = Modifier.clickable {
            annotatedString.getStringAnnotations("URL", 0, annotatedString.length)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        },
        style = TextStyle(fontSize = 16.sp)
    )
}