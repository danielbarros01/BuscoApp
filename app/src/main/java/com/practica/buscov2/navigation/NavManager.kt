package com.practica.buscov2.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.practica.buscov2.model.User
import com.practica.buscov2.ui.viewModel.CheckEmailViewModel
import com.practica.buscov2.ui.viewModel.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.GeorefViewModel
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.LoginViewModel
import com.practica.buscov2.ui.viewModel.RegisterViewModel
import com.practica.buscov2.ui.viewModel.StartViewModel
import com.practica.buscov2.ui.viewModel.TokenViewModel
import com.practica.buscov2.ui.viewModel.UserViewModel
import com.practica.buscov2.ui.views.CheckEmailView
import com.practica.buscov2.ui.views.CompleteDataView
import com.practica.buscov2.ui.views.HomeView
import com.practica.buscov2.ui.views.LoginView
import com.practica.buscov2.ui.views.RegisterView
import com.practica.buscov2.ui.views.StartView


@Composable
fun NavManager() {
    val navController = rememberNavController()
    val tokenViewModel: TokenViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "Start") {
        composable("Start") {
            val startViewModel: StartViewModel = hiltViewModel()
            val userViewModel: UserViewModel = hiltViewModel()
            StartView(startViewModel, tokenViewModel, userViewModel, navController)
        }

        composable("Login") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val userViewModel: UserViewModel = hiltViewModel()
            LoginView(loginViewModel, userViewModel, navController)
        }

        composable(
            "CompleteData/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) {
            val username = it.arguments?.getString("username") ?: ""

            val georefViewModel: GeorefViewModel = hiltViewModel()

            val completeDataViewModel: CompleteDataViewModel = hiltViewModel()
            CompleteDataView(
                completeDataViewModel,
                navController,
                georefViewModel,
                tokenViewModel,
                username
            )
        }

        composable(
            "CheckEmailView/{userJson}",
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) {
            //val email = it.arguments?.getString("email") ?: "su correo."
            val checkEmailViewModel: CheckEmailViewModel = hiltViewModel()

            val userJson = it.arguments?.getString("userJson") ?: ""
            val user = Gson().fromJson(userJson, User::class.java)

            CheckEmailView(checkEmailViewModel, tokenViewModel, navController, user)
        }

        composable("RegisterView") {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterView(registerViewModel, navController)
        }

        composable("Home") {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeView(homeViewModel, navController)
        }
    }
}