package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.views.proposals.ProposalDescription
import com.practica.buscov2.ui.views.proposals.ProposalMoreInfo

sealed class ItemTabOnlyProposal(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (Proposal, Application?, User?, NavController?, SearchMapViewModel) -> Unit
) : TabItem {
    data object TabDescription : ItemTabOnlyProposal(
        title = "Descripción",
        screen = { proposal, application, user, navController, searchMapVM ->
            ProposalDescription(
                proposal,
                user,
                navController
            )
        }
    )

    data object TabMoreInfo : ItemTabOnlyProposal(
        title = "Más info",
        screen = { proposal, application, user, navController, searchMapVM ->
            ProposalMoreInfo(
                proposal,
                application,
                user,
                searchMapVM,
                navController
            )
        }
    )

    companion object {
        val pagesProposal: List<ItemTabOnlyProposal> = listOf(
            TabDescription,
            TabMoreInfo
        )
    }
}