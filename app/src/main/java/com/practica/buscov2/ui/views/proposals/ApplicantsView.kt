package com.practica.buscov2.ui.views.proposals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardApplicant
import com.practica.buscov2.ui.components.CardProposalRecommendation
import com.practica.buscov2.ui.components.CardWorkerRecommendation
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBarWithBack
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax

@Composable
fun ApplicantsView(
    proposalId: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    applicantsViewModel: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    applicantsViewModel.setProposalId(proposalId)
    applicantsViewModel.refreshProposals()

    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let { token ->
            vmUser.getMyProfile(token.token, {
                navController.navigate("Login")
            }) {}

            applicantsViewModel.setToken(token.token)
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ApplicantsV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                applicantsViewModel,
                vmLoading,
                navController
            )
        }
    }
}

@Composable
fun ApplicantsV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    applicantsViewModel: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isLoading by vmLoading.isLoading
    val error by applicantsViewModel.error
    val showError = remember {
        mutableStateOf(false)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    AlertError(showError, "Error", message = error.message)

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                TopBarWithBack(title = "Postulantes", navController = navController)
            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .padding(15.dp)
            ) {
                val applicantsPage = applicantsViewModel.applicantsPage?.collectAsLazyPagingItems()

                if (applicantsPage != null) {
                    activeLoaderMax(applicantsPage, vmLoading)
                    ShowApplicants(applicantsPage, applicantsViewModel, vmLoading, navController,
                        onErrorDecline = {
                            showError.value = true
                        },
                        onErrorAccept = {
                            showError.value = true
                        },
                        onSuccessDecline = {
                            applicantsPage.refresh()
                        }
                    )

                    if (applicantsPage.itemCount == 0) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No hay postulantes", color = GrayText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowApplicants(
    applicantsPage: LazyPagingItems<Application>,
    vmApplications: ApplicationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController,
    onErrorDecline: () -> Unit = {},
    onSuccessDecline: () -> Unit = {},
    onErrorAccept: () -> Unit = {},
    //onSuccessAccept: () -> Unit = {}
) {
    ItemsInLazy(applicantsPage) { item ->
        val idUser = item.worker?.user?.id ?: 0
        val qualification = Qualification(5f, 20)

        CardApplicant(
            item,
            qualification,
            onClickName = {
                navController.navigate("Profile/${idUser}")
            },
            onDecline = {
                vmLoading.withLoading {
                    vmApplications.acceptOrDeclineApplication(
                        item.proposalId!!,
                        item.id!!,
                        false,
                        onError = {
                            //Mostrar error
                            onErrorDecline()
                        },
                        onSuccess = {
                            //Actualizar la lista
                            onSuccessDecline()
                        })
                }
            },
            onChoose = {
                vmLoading.withLoading {
                    vmApplications.acceptOrDeclineApplication(
                        item.proposalId!!,
                        item.id!!,
                        true,
                        onError = {
                            //Mostrar error
                            onErrorAccept()
                        },
                        onSuccess = {
                            //Ir a propuesta
                            navController.navigate("Proposal/${item.proposalId}")
                        })
                }
            }
        )

        Space(size = 8.dp)
    }
}