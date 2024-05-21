package com.practica.buscov2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.practica.buscov2.R

@Composable
fun Logo(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "Header",
        modifier = modifier,
    )
}

@Composable
fun SeparatoryLine() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(modifier = Modifier.weight(1f), thickness = 1.dp)
        Text(text = "o", modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        Divider(modifier = Modifier.weight(1f), thickness = 1.dp)
    }
}

@Composable
fun Space(size: Dp){
    Spacer(modifier = Modifier.padding(size))
}