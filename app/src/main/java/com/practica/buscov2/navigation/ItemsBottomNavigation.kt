package com.practica.buscov2.navigation

import android.media.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.practica.buscov2.R

sealed class RoutesBottom(
    val icon: Int,
    val iconSelected: Int,
    val title: String,
    val route: String
) {
    object Home : RoutesBottom(R.drawable.home, R.drawable.home_fill, "Inicio", "Home")
    object New : RoutesBottom(R.drawable.add,R.drawable.add_fill, "Nuevo", "New")
    object Chat : RoutesBottom(R.drawable.message,R.drawable.message_fill, "Nuevo", "Chat")
}
