package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesConfiguration(
    val icon: Int,
    val title: String,
    val route: String
) {
    //object Profile : RoutesConfiguration(R.drawable.profile, "Ver Perfil", "Profile")

    data class Profile(val userId: Int) : RoutesConfiguration(
        R.drawable.profile,
        "Ver Perfil",
        "Profile/${userId}"
    )

    data class ChangePassword(val userJson: String) : RoutesConfiguration(
        R.drawable.privacy,
        "Cambiar contraseña",
        "RecoverPassword"
        //"CheckEmailView/$userJson/recover-password"
    )

    companion object {
        val allRoutes: List<RoutesConfiguration> = listOf(
            //Profile,
            //ChangePassword,
        )
    }
}
