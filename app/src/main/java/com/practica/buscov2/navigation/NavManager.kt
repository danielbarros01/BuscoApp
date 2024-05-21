package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.practica.buscov2.ui.viewModel.LoginViewModel
import com.practica.buscov2.ui.views.LoginView

@Composable
fun NavManager(viewModel: LoginViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Login") {
        composable("Login") {
            LoginView(viewModel, navController)
        }
    }
}