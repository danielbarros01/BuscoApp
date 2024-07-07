package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesConfiguration(
    val icon: Int,
    val title: String,
    val route: String
){
    object Profile : RoutesConfiguration(R.drawable.profile, "Ver Perfil", "Profile")
    object ChangePassword : RoutesConfiguration(R.drawable.privacy, "Cambiar contraseña", "ChangePassword")

    companion object{
        val allRoutes: List<RoutesConfiguration> = listOf(
            Profile,
            ChangePassword,
        )
    }
}
