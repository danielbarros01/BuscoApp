package com.practica.buscov2.ui.views.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.CardChatUser
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.chat.ChatViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {}
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatPrincipal(
                Modifier.align(Alignment.Center),
                viewModel,
                vmUser,
                vmGoogle,
                navController,
                user!!,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatPrincipal(
    modifier: Modifier,
    viewModel: ChatViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    navController: NavHostController,
    user: User
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
                TopBar(title = "Chats", scope = scope, drawerState = drawerState)
            })
        {
            //Debo traer los chats
            val chats by viewModel.chats.collectAsState()
            LazyColumn(
                modifier = modifier
                    .padding(it)
                    .padding(15.dp)
                    .fillMaxSize()
            ) {
                items(chats.size) { index ->
                    val chat = chats[index]
                    CardChatUser(chat){
                        navController.navigate("Chat/${chat.user?.id}")
                    }
                }
            }
        }
    }
}



