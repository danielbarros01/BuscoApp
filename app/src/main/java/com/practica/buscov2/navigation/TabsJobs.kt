package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.Jobs
import com.practica.buscov2.ui.views.proposals.ActiveProposals
import com.practica.buscov2.ui.views.proposals.ExpiredProposals

sealed class ItemTabJob(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vmJobs: JobsViewModel, vmLoading: LoadingViewModel, navController: NavHostController) -> Unit
) : TabItem {
    object tab_activated : ItemTabJob(
        title = "En proceso",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )

    object tab_finished : ItemTabJob(
        title = "Finalizados",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )


    companion object {
        val pagesJobs = listOf(tab_activated, tab_finished)
    }
}