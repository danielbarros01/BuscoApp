package com.practica.buscov2.ui.views

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.ItemTabApplication
import com.practica.buscov2.navigation.ItemTabJob
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardJob
import com.practica.buscov2.ui.components.CardProposal
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
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax
import com.practica.buscov2.util.AppUtils

@Composable
fun ApplicationsView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmApplications: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let { it ->
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) { user ->
                //Traer propuestas
                user.id?.let {}
            }

            vmApplications.setToken(it.token)
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ApplicationsV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                vmApplications,
                vmLoading,
                user!!,
                navController
            )
        }
    }
}

@Composable
fun ApplicationsV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmApplications: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    user: User,
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
                TopBar(title = "Postulaciones", scope = scope, drawerState = drawerState)
            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                TabsPages(vmApplications, vmLoading, navController)
            }
        }
    }
}

@Composable
fun ListApplicationsView(
    vm: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val applicationsPage = vm.applicationsPage.collectAsLazyPagingItems()
    activeLoaderMax(applicationsPage, vmLoading)

    if (applicationsPage.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No encontramos postulaciones para mostrar", color = GrayText)
        }
    } else {
        ShowApplications(applicationsPage, navController)
    }
}

@Composable
fun ShowApplications(applicationsPage: LazyPagingItems<Application>, navController: NavController) {
    ItemsInLazy(applicationsPage) {
        //CARD
        val image = it.proposal?.image ?: ""
        val title = it.proposal?.title ?: ""
        val price = "$${it.proposal?.minBudget.toString()} a $${it.proposal?.maxBudget.toString()}"
        val date = AppUtils.formatDateCard("${it.proposal?.date}")

        CardProposal(image, title, price, date) {
            navController.navigate("Proposal/${it.proposalId}")
        }
    }
}

@Composable
private fun TabsPages(
    vm: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val tabs = ItemTabApplication.pagesApplications
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column {
        TabsComponent(tabs, pagerState, fontSize = 16.sp) { index ->
            //Cambiar el status, status true trae las aceptadas
            val status = when (tabs[index].title) {
                "Aceptadas" -> true
                "Pendientes" -> null
                else -> false // Propuestas rechazadas
            }
            vm.changeStatus(status)
            vm.refreshProposals()
        }

        TabsContent(tabs, pagerState, vm, vmLoading, navController)
    }
}

@Composable
private fun TabsContent(
    tabs: List<ItemTabApplication>,
    pagerState: PagerState,
    vm: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(vm, vmLoading, navController)
    }
}

