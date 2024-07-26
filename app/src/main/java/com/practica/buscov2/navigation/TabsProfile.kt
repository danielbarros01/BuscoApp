package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.viewModel.QualificationsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.users.Information
import com.practica.buscov2.ui.views.users.Proposals
import com.practica.buscov2.ui.views.users.Qualifications
import com.practica.buscov2.ui.views.users.WorksCompletedProfile

sealed class ItemTabProfile(
    var title: String,
    var icon: Int? = null,
    var screen: @Composable (User, ProposalsViewModel, NavController, QualificationsViewModel) -> Unit
) {
    object tab_information : ItemTabProfile(
        title = "Información",
        screen = { user, vmProposals, navController, vmQualifications -> Information(user) }
    )

    object tab_proposals : ItemTabProfile(
        title = "Propuestas",
        icon = R.drawable.hand,
        screen = { user, vmProposals, navController, vmQualifications ->
            Proposals(
                vmProposals,
                navController
            )
        }
    )

    object tab_qualificatios : ItemTabProfile(
        title = "Calificaciones",
        screen = { user, vmProposals, navController, vmQualifications ->
            Qualifications(
                vmQualifications,
                vmProposals
            )
        }
    )

    object tab_jobs : ItemTabProfile(
        title = "Trabajos",
        screen = { user, vmProposals, navController, vmQualifications -> WorksCompletedProfile(user) }
    )

    companion object {
        val pagesUser: List<ItemTabProfile> = listOf(
            tab_information,
            tab_proposals
        )

        val pagesWorker: List<ItemTabProfile> = listOf(
            tab_information,
            tab_proposals,
            tab_qualificatios,
            tab_jobs
        )
    }
}
