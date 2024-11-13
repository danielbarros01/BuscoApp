package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesDrawer(
    val icon: Int,
    val title: String,
    val route: String
){
    data object Works : RoutesDrawer(R.drawable.deal, "Trabajos", "Jobs/me")
    data object Applications : RoutesDrawer(R.drawable.briefcase_works, "Postulaciones", "Applications/me")
    data object Proposals : RoutesDrawer(R.drawable.hand,"Propuestas", "Proposals")
    data object Notifications : RoutesDrawer(R.drawable.notification, "Notificaciones", "Notifications")

    companion object{
        val allRoutes: List<RoutesDrawer> = listOf(
            Works,
            Applications,
            Proposals,
            Notifications
        )
    }
}
