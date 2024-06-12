package com.practica.buscov2.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.practica.buscov2.R

@Composable
fun InsertImage(image: Int, modifier: Modifier) {
    Image(
        painter = painterResource(id = image),
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
fun Space(size: Dp) {
    Spacer(modifier = Modifier.padding(size))
}

@Composable
fun LoaderMaxSize(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .zIndex(2f)
            .clickable{},
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 10.dp,
            modifier = Modifier.size(80.dp)
        )
    }
}
