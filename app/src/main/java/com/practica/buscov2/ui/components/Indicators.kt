package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.OrangePrincipal

@Composable
fun PagerIndicator(size: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp)
    ) {
        repeat(size) {
            Indicator(it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelect: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 5.dp)
            .height(20.dp)
            .width(20.dp)
            .clip(CircleShape)
            .background(if (isSelect) OrangePrincipal else GrayPlaceholder)
    )
    {

    }
}