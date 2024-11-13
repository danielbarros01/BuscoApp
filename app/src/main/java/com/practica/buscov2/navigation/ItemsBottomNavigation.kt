package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesBottom(
    val icon: Int,
    val iconSelected: Int,
    val title: String,
    val route: String
) {
    data object Home : RoutesBottom(R.drawable.home, R.drawable.home_fill, "Inicio", "Home")
    data object New : RoutesBottom(R.drawable.add, R.drawable.add_fill, "Nueva Publicacion", "New")
    data object Chat : RoutesBottom(R.drawable.message, R.drawable.message_fill, "Chat", "Chat")

    companion object {
        val allRoutes: List<RoutesBottom> = listOf(Home, New, Chat)
    }
}
