package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.views.Jobs
import com.practica.buscov2.ui.views.ListApplicationsView

sealed class ItemTabApplication(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vm: ApplicationsViewModel, navController: NavHostController) -> Unit
) : TabItem {
    object tab_accepted : ItemTabApplication(
        title = "Aceptadas",
        screen = { vm, navController -> ListApplicationsView(vm, navController) }
    )

    object tab_pending : ItemTabApplication(
        title = "Pendientes",
        screen = { vm, navController -> ListApplicationsView(vm, navController) }
    )

    object tab_rejected : ItemTabApplication(
        title = "Rechazadas",
        screen = { vm, navController -> ListApplicationsView(vm, navController) }
    )


    companion object {
        val pagesApplications = listOf(tab_accepted, tab_pending,tab_rejected)
    }
}