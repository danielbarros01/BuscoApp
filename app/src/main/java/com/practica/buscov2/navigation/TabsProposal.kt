package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.views.proposals.ProposalDescription
import com.practica.buscov2.ui.views.proposals.ProposalMoreInfo

sealed class ItemTabOnlyProposal(
    override var title: String,
    override var icon: Int? = null,
    var screen: @Composable (Proposal, Application?, User?, NavController?) -> Unit
) : TabItem {
    object tab_description : ItemTabOnlyProposal(
        title = "Descripción",
        screen = { proposal, application, user, navController ->
            ProposalDescription(
                proposal,
                user,
                navController
            )
        }
    )

    object tab_moreinfo : ItemTabOnlyProposal(
        title = "Más info",
        screen = { proposal, application, user, navController ->
            ProposalMoreInfo(
                proposal,
                application,
                user,
                navController
            )
        }
    )

    companion object {
        val pagesProposal: List<ItemTabOnlyProposal> = listOf(
            tab_description,
            tab_moreinfo
        )
    }
}