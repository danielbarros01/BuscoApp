package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.views.Jobs

sealed class ItemTabJob(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vmJobs: JobsViewModel, vmLoading: LoadingViewModel, navController: NavHostController) -> Unit
) : TabItem {
    data object TabActivated : ItemTabJob(
        title = "En proceso",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )

    data object TabFinished : ItemTabJob(
        title = "Finalizados",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )


    companion object {
        val pagesJobs = listOf(TabActivated, TabFinished)
    }
}