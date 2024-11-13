package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.QualificationsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.views.users.Information
import com.practica.buscov2.ui.views.users.Proposals
import com.practica.buscov2.ui.views.users.Qualifications
import com.practica.buscov2.ui.views.users.WorksCompletedProfile

sealed class ItemTabProfile(
    var title: String,
    var icon: Int? = null,
    var screen: @Composable (User, ProposalsViewModel, NavController, QualificationsViewModel, JobsViewModel, LoadingViewModel, SearchMapViewModel) -> Unit
) {
    data object TabInformation : ItemTabProfile(
        title = "Información",
        screen = { user, vmProposals, navController, vmQualifications, vmJobs, vmLoading, searchMapVM ->
            Information(
                searchMapVM,
                user
            )
        }
    )

    data object TabProposals : ItemTabProfile(
        title = "Propuestas",
        icon = R.drawable.hand,
        screen = { user, vmProposals, navController, vmQualifications, vmJobs, vmLoading, searchMapVM ->
            Proposals(
                vmProposals,
                vmLoading,
                navController
            )
        }
    )

    data object TabQualifications : ItemTabProfile(
        title = "Calificaciones",
        screen = { user, vmProposals, navController, vmQualifications, vmJobs, vmLoading, searchMapVM ->
            Qualifications(
                vmQualifications,
                vmLoading
            )
        }
    )

    data object TabJobs : ItemTabProfile(
        title = "Trabajos",
        screen = { user, vmProposals, navController, vmQualifications, vmJobs, vmLoading, searchMapVM ->
            WorksCompletedProfile(
                vmJobs,
                vmLoading,
                navController
            )
        }
    )

    companion object {
        val pagesUser: List<ItemTabProfile> = listOf(
            TabInformation,
            TabProposals
        )

        val pagesWorker: List<ItemTabProfile> = listOf(
            TabInformation,
            TabProposals,
            TabQualifications,
            TabJobs
        )
    }
}
