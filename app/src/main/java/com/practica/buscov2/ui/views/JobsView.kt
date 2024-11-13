package com.practica.buscov2.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.ItemTabJob
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardJob
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.TabsComponent
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JobsView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProposals: ProposalsViewModel,
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) { user ->
                //Traer propuestas
                user.id?.let { id ->
                    vmProposals.changeUserId(id)
                }
            }


            vmJobs.setToken(it.token)
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            JobsV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                vmJobs,
                user!!,
                vmLoading,
                navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JobsV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmJobs: JobsViewModel,
    user: User,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isLoading = vmLoading.isLoading

    if (isLoading.value) {
        LoaderMaxSize()
    }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) { scope ->
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                TopBar(title = "Trabajos", scope = scope, drawerState = drawerState)
            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                TabsPages(vmJobs, vmLoading, navController)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Jobs(vmJobs: JobsViewModel, vmLoading: LoadingViewModel, navController: NavController) {
    val jobsPage = vmJobs.jobsPage.collectAsLazyPagingItems()
    activeLoaderMax(jobsPage, vmLoading)

    if (jobsPage.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No hay trabajos que mostrar", color = GrayText)
        }
    } else {
        ShowJobs(jobsPage, navController)
    }
}

/*@Composable
fun FinishedJobs(vmJobs: JobsViewModel, navController: NavController) {
    val jobsPage = vmJobs.jobsPage.collectAsLazyPagingItems()
    activeLoaderMaxJobs(jobsPage, vmJobs)

    if (jobsPage.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No hay trabajos que mostrar", color = GrayText)
        }
    } else {
        ShowJobs(jobsPage, navController)
    }
}*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ShowJobs(jobsPage: LazyPagingItems<Proposal>, navController: NavController) {
    ItemsInLazy(jobsPage) { proposal ->
        val worker = proposal.applications?.firstOrNull()?.worker
        val user = worker?.user
        //CARD
        if (worker != null && user != null) {
            CardJob(proposal, user, worker,
                onClickProposal = {
                    navController.navigate("Proposal/${proposal.id}")
                },
                onClickName = {
                    navController.navigate("Profile/${user.id}")
                },
                onClickChat = {
                    //Ir al chat
                    navController.navigate("Chat/${user.id}")
                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TabsPages(
    vm: JobsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val tabs = ItemTabJob.pagesJobs
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column {
        TabsComponent(tabs, pagerState) { index ->
            //Cambiar el status de la propuesta, status true trae las terminadas
            vm.changeIsFinished(tabs[index].title != "En proceso")
            vm.refreshProposals()
        }
        TabsContent(tabs, pagerState, vm, vmLoading, navController)
    }
}

@Composable
private fun TabsContent(
    tabs: List<ItemTabJob>,
    pagerState: PagerState,
    vm: JobsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(vm, vmLoading, navController)
    }
}
