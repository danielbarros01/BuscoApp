package com.practica.buscov2.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.navigation.RoutesConfiguration
import com.practica.buscov2.navigation.RoutesDrawer
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonMenu
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ConfigurationView(
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
            ConfigurationV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationV(
    modifier: Modifier,
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
                TopBar(title = "Configuración", scope = scope, drawerState = drawerState)
            }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 15.dp, end = 15.dp, bottom = 15.dp, top = 5.dp)
            ) {
                RoutesConfiguration.allRoutes.forEach { item ->
                    ItemConfiguration(item = item){
                        navController.navigate(item.route)
                    }
                }

                // Agregar ChangePassword como un caso especial
                val userJson = Gson().toJson(User(email = user.email))
                val changePasswordRoute = RoutesConfiguration.ChangePassword(userJson).route
                ItemConfiguration(item = RoutesConfiguration.ChangePassword(userJson)) {
                    navController.navigate(changePasswordRoute)
                }

            }
        }
    }
}

@Composable
fun ItemConfiguration(modifier: Modifier = Modifier, item: RoutesConfiguration, onClick: () -> Unit){
    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(15.dp))
        .clickable { onClick() }){
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = item.title,
                tint = GrayText,
                modifier = Modifier.size(44.dp)
            )
            Space(size = 5.dp)
            Text(text = item.title, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = GrayText)
        }
    }
}