package com.practica.buscov2.ui.views.proposals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.Notification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.notifications.NotificationService
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardProposalWithButton
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBarWithBack
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.NotificationsViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActiveProposalsView(
    userIdToSend: Int,
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmProposals: ProposalsViewModel,
    vmNotifications: NotificationsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let { token ->
            //Mi perfil
            vmUser.getMyProfile(token.token, {
                navController.navigate("Login")
            }) {
                vmProposals.changeUserId(it.id!!)

            }
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ActiveProposalsV(
                Modifier.align(Alignment.Center),
                vmProposals,
                user!!,
                vmNotifications,
                userIdToSend,
                vmLoading,
                navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActiveProposalsV(
    modifier: Modifier,
    vmProposals: ProposalsViewModel,
    user: User,
    vmNotifications: NotificationsViewModel,
    userIdToSend: Int,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val isLoading by vmLoading.isLoading

    if (isLoading) {
        LoaderMaxSize()
    }

    Scaffold(modifier = Modifier
        .fillMaxSize(),
        bottomBar = {
            BottomNav(navController, RoutesBottom.allRoutes)
        },
        topBar = {
            TopBarWithBack(title = "Propuestas activas", navController = navController)
        }) {
        Column(
            modifier = modifier
                .padding(it)
                .padding(15.dp)
        ) {
            val proposalsPage = vmProposals.proposalsPage.collectAsLazyPagingItems()
            activeLoaderMax(proposalsPage, vmLoading)

            if (proposalsPage.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay propuestas activas para enviar", color = GrayText)
                }
            } else {
                ItemsInLazy(itemsPage = proposalsPage) { proposal ->
                    //CARDS
                    CardProposalWithButton(proposal, onClickProposal = {
                        navController.navigate("Proposal/${proposal.id}")
                    }, onSend = { text, enabledButton ->
                        //Enviar propuesta
                        //En notificacion
                        vmNotifications.sendNotification(
                            Notification(
                                userSenderId = user.id,
                                userReceiveId = userIdToSend,
                                text = "${user.username} te ha enviado una propuesta",
                                proposalId = proposal.id
                            )
                        )
                        //En mensaje

                        //Exito
                        text.value = "Propuesta enviada"
                        enabledButton.value = false
                    })

                    Space(size = 10.dp)
                }
            }
        }
    }
}