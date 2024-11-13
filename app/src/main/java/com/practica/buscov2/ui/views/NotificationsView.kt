package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
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
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardNotification
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.viewModel.NotificationsViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel

@Composable
fun NotificationsView(
    vmNotifications: NotificationsViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    navController: NavHostController
) {
    val user by vmUser.user.collectAsState()
    val token by vmNotifications.token.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        vmNotifications.token.value?.token?.let {
            vmUser.getMyProfile(it, {
                navController.navigate("Login")
            }) {}
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            NotificationsV(
                Modifier.align(Alignment.Center),
                vmNotifications,
                vmUser,
                vmGoogle,
                user!!,
                navController
            )
        }
    }
}

@Composable
fun NotificationsV(
    modifier: Modifier,
    vmNotifications: NotificationsViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

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
                TopBar(title = "Notificaciones", scope = scope, drawerState = drawerState)
            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 5.dp)
            ) {
                val notificationsPage = vmNotifications.notificationsPage.collectAsLazyPagingItems()

                if (notificationsPage.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Sin notificaciones", color = GrayText)
                    }
                } else {
                    ItemsInLazy(itemsPage = notificationsPage) { notification ->
                        CardNotification(notification) {
                            if(notification.proposalId != null){
                                navController.navigate("Proposal/${notification.proposalId}")
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
