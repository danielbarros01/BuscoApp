package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav

@Composable
fun JobsView(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        JobsV(Modifier.align(Alignment.Center), navController)
    }
}

@Composable
fun JobsV(modifier: Modifier, navController: NavHostController) {
//Rutas de navegacion
    val navigationRoutes = listOf(
        RoutesBottom.Home,
        RoutesBottom.New,
        RoutesBottom.Chat
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            //MenuNavigation(homeVm = homeVm, vmGoogle = vmGoogle, navController = navController)
        }
    ) {
        //Screen content
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, navigationRoutes)
            }) {
            Column(modifier = Modifier.padding(it)) {
                Text(text = "Trabajos")
            }
        }
    }
}