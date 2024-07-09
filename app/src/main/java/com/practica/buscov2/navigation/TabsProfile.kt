package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.views.users.Information
import com.practica.buscov2.ui.views.users.Proposals
import com.practica.buscov2.ui.views.users.Qualifications
import com.practica.buscov2.ui.views.users.WorksCompletedProfile

sealed class ItemTabProfile(
    var title: String,
    var icon: Int? = null,
    var screen: @Composable (User) -> Unit
) {
    object tab_information : ItemTabProfile(
        title = "Información",
        screen = { user -> Information(user) }
    )

    object tab_proposals : ItemTabProfile(
        title = "Propuestas",
        icon = R.drawable.hand,
        screen = { user -> Proposals(user) }
    )

    object tab_qualificatios : ItemTabProfile(
        title = "Calificaciones",
        screen = { user -> Qualifications(user) }
    )

    object tab_jobs : ItemTabProfile(
        title = "Trabajos",
        screen = { user -> WorksCompletedProfile(user) }
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
