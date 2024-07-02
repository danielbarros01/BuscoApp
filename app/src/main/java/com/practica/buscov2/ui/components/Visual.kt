package com.practica.buscov2.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.util.Constants.Companion.API_URL
import kotlin.io.path.Path

@Composable
fun InsertImage(image: Int, modifier: Modifier) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "Header",
        modifier = modifier,
    )
}

@Composable
fun InsertCircleProfileImage(image: String, modifier: Modifier, onClick: () -> Unit = {}) {
    val defaultImage = painterResource(id = R.drawable.userpicnull)

    val imageUrl = if(image.contains("localhost")){
        image.replace("localhost", API_URL)
    } else {
        image
    }


    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Foto de perfil",
        placeholder = defaultImage,
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape)
            .border(1.dp, GrayPlaceholder, CircleShape)
            .clickable { onClick() },
        contentScale = ContentScale.Crop,
        onError = {
            Log.d("ERROR", it.toString())
        }
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
fun LoaderMaxSize() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .zIndex(2f)
            .clickable {},
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 10.dp,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun Circle(modifier: Modifier = Modifier, color: Color, size: Dp) {
    Box(
        modifier = modifier
            .size(size) // Tamaño del círculo
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun TriangleShape(modifier: Modifier, color: Color) {
    Canvas(modifier = modifier, onDraw = {
        val trianglePath = androidx.compose.ui.graphics.Path().apply {
            // Moves to top center position
            moveTo(size.width / 2f, size.height / 6)
            // Add line to bottom right corner
            lineTo(size.width, size.height)
            // Add line to bottom left corner
            lineTo(0f, size.height)
        }

        drawPath(
            color = color,
            path = trianglePath
        )
    })
}