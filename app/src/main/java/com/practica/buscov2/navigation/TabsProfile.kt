package com.practica.buscov2.navigation

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.views.Information
import com.practica.buscov2.ui.views.Proposals
import com.practica.buscov2.ui.views.Qualifications
import com.practica.buscov2.ui.views.WorksCompletedProfile

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
