package com.practica.buscov2.ui.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.YellowStar
import com.practica.buscov2.util.Constants.Companion.API_URL

@Composable
fun InsertImage(image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = image),
        contentDescription = "Header",
        modifier = modifier,
    )
}

@Composable
fun InsertAsyncImage(
    image: String,
    defaultImg: Int = R.drawable.userpicnull,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onClick: () -> Unit = {}
) {
    val defaultImage = painterResource(id = defaultImg)

    val imageUrl = if (image.contains("localhost")) {
        image.replace("localhost", API_URL)
    } else {
        image
    }

    if (image.isNotEmpty()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Foto de perfil",
            placeholder = defaultImage,
            modifier = modifier.clickable { onClick() },
            contentScale = contentScale,
            onError = {
                Log.d("ERROR", it.toString())
            }
        )
    } else {
        Image(
            painter = defaultImage, contentDescription = "",
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun InsertCircleProfileImage(image: String, modifier: Modifier, onClick: () -> Unit = {}) {
    val imagePainter = painterResource(id = R.drawable.userpicnull)

    val imageUrl = if (image.contains("localhost")) {
        image.replace("localhost", API_URL)
    } else {
        image
    }

    val request = if (image.isNotEmpty()) {
        ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build()
    } else {
        null
    }

    AsyncImage(
        model = request,
        contentDescription = "Foto de perfil",
        placeholder = imagePainter,
        error = imagePainter,
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape)
            .border(1.dp, GrayPlaceholder, CircleShape)
            .clickable { onClick() },
        contentScale = ContentScale.Crop
    )
}


@Composable
fun InsertCirlceProfileEditImage(image: String, modifier: Modifier, onClick: () -> Unit) {
    Box {
        InsertCircleProfileImage(image = image, modifier = modifier, onClick = onClick)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.5f))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Editar Foto",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
                tint = GrayText
            )
        }
    }
}


@Composable
fun SeparatoryLine() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
        Text(text = "o", modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
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
            color = OrangePrincipal,
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

@Composable
fun <T : Any> ItemsInLazy(
    itemsPage: LazyPagingItems<T>,
    secondViewHeader: @Composable () -> Unit = {},
    view: @Composable (T) -> Unit
) {
    LazyColumn {
        item {
            secondViewHeader()
        }

        items(itemsPage.itemCount) { index ->
            val item = itemsPage[index]
            if (item != null) {
                view(item)
            }
        }
        when (itemsPage.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 6.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    Text(text = "Error al cargar")
                }
            }
        }
    }
}

@Composable
fun InfiniteRotationIcon(modifier: Modifier = Modifier, iconId: Int = R.drawable.working) {
    val rotation = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Icon(
        painter = painterResource(id = iconId),
        contentDescription = "",
        tint = GreenBusco,
        modifier = modifier.graphicsLayer { rotationZ = rotation.value })
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    sizeStar: Dp = 36.dp,
    onRatingChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) YellowStar else GrayPlaceholder
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .size(sizeStar)
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
            )
        }
    }
}