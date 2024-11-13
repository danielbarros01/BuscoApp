package com.practica.buscov2.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
    @RequiresApi(Build.VERSION_CODES.O)
    data object TabActivated : ItemTabJob(
        title = "En proceso",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    data object TabFinished : ItemTabJob(
        title = "Finalizados",
        screen = { vm, vmLoading, navController -> Jobs(vm, vmLoading, navController) }
    )


    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        val pagesJobs = listOf(TabActivated, TabFinished)
    }
}