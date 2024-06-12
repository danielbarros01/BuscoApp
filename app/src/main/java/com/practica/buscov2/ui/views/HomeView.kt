package com.practica.buscov2.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.viewModel.HomeViewModel

@Composable
fun HomeView(homeVm: HomeViewModel, navController: NavController){
    Box(
    ) {
        ButtonPrincipal(text = "Cerrar Sesion", enabled = true) {
            homeVm.logout {
                navController.navigate("Login") {
                    popUpTo("Home") { inclusive = true } // asegura que se elimine la pantalla "Home"
                }
            }
        }
    }
}