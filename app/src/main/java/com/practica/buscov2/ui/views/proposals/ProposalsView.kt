package com.practica.buscov2.ui.views.proposals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.ItemTabProposal
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardProposal
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.RedBusco
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax
import com.practica.buscov2.util.AppUtils.Companion.formatDateCard
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProposalsView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProposals: ProposalsViewModel,
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
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProposalsV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                vmProposals,
                vmLoading,
                navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProposalsV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    vmProposals: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val isLoading by vmLoading.isLoading
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (isLoading) {
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
                TopBar(title = "Propuestas", scope = scope, drawerState = drawerState)
            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .padding(15.dp)
            ) {
                TabsPages(vmProposals, vmLoading, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpiredProposals(
    vmProposals: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val proposalsPage = vmProposals.proposalsPage.collectAsLazyPagingItems()
    activeLoaderMax(proposalsPage, vmLoading)

    if (proposalsPage.itemCount == 0) {
        NoProposals()
    } else {
        ShowProposals(proposalsPage, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActiveProposals(
    vmProposals: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val proposalsPage = vmProposals.proposalsPage.collectAsLazyPagingItems()
    activeLoaderMax(proposalsPage, vmLoading)

    if (proposalsPage.itemCount == 0) {
        NoProposals()
    } else {
        ShowProposals(proposalsPage, navController)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowProposals(proposalsPage: LazyPagingItems<Proposal>, navController: NavController) {
    ItemsInLazy(proposalsPage) { item ->
        CardProposal(
            image = item.image ?: "",
            title = item.title ?: "",
            price = "$${item.minBudget.toString()} a $${item.maxBudget.toString()}",
            date = formatDateCard("${item.date}"),
        ) {
            //Onclick
            navController.navigate("Proposal/${item.id}")
        }
    }
}

@Composable
fun NoProposals() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No hay propuestas que mostrar", color = GrayText)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TabsPages(
    vmProposals: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val tabs = ItemTabProposal.pagesProposals
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column {
        Tabs(tabs, pagerState, vmProposals)
        TabsContent(tabs, pagerState, vmProposals, vmLoading, navController)
    }
}


@Composable
private fun Tabs(
    tabs: List<ItemTabProposal>,
    pagerState: PagerState,
    vmProposals: ProposalsViewModel
) {
    val selectedTab = pagerState.currentPage
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            indicator = {}
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        //null trae activas, true las terminadas
                        vmProposals.changeStatus(if (tabs[index].title == "Activas") null else true)
                        vmProposals.refreshProposals()
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = 18.sp,
                            color = if (index == selectedTab
                                && (item.title == "Activas")
                            ) GreenBusco
                            else if (index == selectedTab
                                && (item.title == "Finalizadas")
                            ) RedBusco
                            else GrayText
                        )
                    }
                )
            }
        }
    }

}

@Composable
private fun TabsContent(
    tabs: List<ItemTabProposal>,
    pagerState: PagerState,
    vm: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(vm, vmLoading, navController)
    }
}



