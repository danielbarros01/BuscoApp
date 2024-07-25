package com.practica.buscov2.navigation

import android.app.ActionBar.Tab
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.proposals.ActiveProposals
import com.practica.buscov2.ui.views.proposals.ExpiredProposals

sealed class ItemTabProposal(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (vmProposals: ProposalsViewModel, navController: NavHostController) -> Unit
) : TabItem {
    object tab_activated : ItemTabProposal(
        title = "Activas",
        screen = { vm, navController -> ActiveProposals(vm, navController) }
    )

    object tab_finished : ItemTabProposal(
        title = "Finalizadas",
        screen = { vm, navController -> ExpiredProposals(vm, navController) }
    )


    companion object {
        val pagesProposals = listOf(tab_activated, tab_finished)
    }
}