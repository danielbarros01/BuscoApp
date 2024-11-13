package com.practica.buscov2.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.proposals.ActiveProposals
import com.practica.buscov2.ui.views.proposals.ExpiredProposals

sealed class ItemTabProposal(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vmProposals: ProposalsViewModel, vmLoading: LoadingViewModel, navController: NavHostController) -> Unit
) : TabItem {
    @RequiresApi(Build.VERSION_CODES.O)
    data object TabActivated : ItemTabProposal(
        title = "Activas",
        screen = { vm, vmLoading, navController -> ActiveProposals(vm, vmLoading, navController) }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    data object TabFinished : ItemTabProposal(
        title = "Finalizadas",
        screen = { vm, vmLoading, navController -> ExpiredProposals(vm, vmLoading, navController) }
    )


    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val pagesProposals = listOf(TabActivated, TabFinished)
    }
}