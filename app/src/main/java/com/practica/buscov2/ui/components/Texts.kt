package com.practica.buscov2.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practica.buscov2.ui.theme.GrayText

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String,
    size: TextUnit = 24.sp,
    color: Color = GrayText,
    verticalPadding: Dp = 15.dp,
    textAlign: TextAlign =TextAlign.Center
) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = size,
        modifier = modifier
            .fillMaxWidth(),
        textAlign = textAlign,
        color = color
    )
}