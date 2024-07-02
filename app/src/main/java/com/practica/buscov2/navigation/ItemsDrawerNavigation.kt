package com.practica.buscov2.navigation

import com.practica.buscov2.R

sealed class RoutesDrawer(
    val icon: Int,
    val title: String,
    val route: String
){
    object Works : RoutesDrawer(R.drawable.deal, "Trabajos", "Works")
    object Applications : RoutesDrawer(R.drawable.briefcase_works, "Postulaciones", "Applications")
    object Proposals : RoutesDrawer(R.drawable.hand,"Propuestas", "Proposals")
    object Notifications : RoutesDrawer(R.drawable.notification, "Notificaciones", "Notifications")
}
