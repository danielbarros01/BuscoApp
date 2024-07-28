package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesDrawer(
    val icon: Int,
    val title: String,
    val route: String
){
    object Works : RoutesDrawer(R.drawable.deal, "Trabajos", "Jobs/me")
    object Applications : RoutesDrawer(R.drawable.briefcase_works, "Postulaciones", "Applications/me")
    object Proposals : RoutesDrawer(R.drawable.hand,"Propuestas", "Proposals")
    object Notifications : RoutesDrawer(R.drawable.notification, "Notificaciones", "Notifications")

    companion object{
        val allRoutes: List<RoutesDrawer> = listOf(
            Works,
            Applications,
            Proposals,
            Notifications
        )
    }
}
