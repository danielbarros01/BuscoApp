package com.practica.buscov2.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.views.ListApplicationsView

sealed class ItemTabApplication(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vm: ApplicationsViewModel, vmLoading: LoadingViewModel, navController: NavHostController) -> Unit
) : TabItem {
    @RequiresApi(Build.VERSION_CODES.O)
    data object TabAccepted : ItemTabApplication(
        title = "Aceptadas",
        screen = { vm, vmLoading, navController ->
            ListApplicationsView(
                vm,
                vmLoading,
                navController
            )
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    data object TabPending : ItemTabApplication(
        title = "Pendientes",
        screen = { vm, vmLoading, navController ->
            ListApplicationsView(
                vm,
                vmLoading,
                navController
            )
        }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    data object TabRejected : ItemTabApplication(
        title = "Rechazadas",
        screen = { vm, vmLoading, navController ->
            ListApplicationsView(
                vm,
                vmLoading,
                navController
            )
        }
    )


    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val pagesApplications = listOf(TabAccepted, TabPending, TabRejected)
    }
}