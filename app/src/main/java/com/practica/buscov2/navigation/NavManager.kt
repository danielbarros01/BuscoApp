package com.practica.buscov2.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.practica.buscov2.ui.viewModel.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.LoginViewModel
import com.practica.buscov2.ui.viewModel.RecoverPasswordViewModel
import com.practica.buscov2.ui.viewModel.RegisterViewModel
import com.practica.buscov2.ui.viewModel.ResetPasswordViewModel
import com.practica.buscov2.ui.viewModel.StartViewModel
import com.practica.buscov2.ui.viewModel.TokenViewModel
import com.practica.buscov2.ui.viewModel.UserViewModel
import com.practica.buscov2.ui.views.CheckEmailView
import com.practica.buscov2.ui.views.CompleteDataView
import com.practica.buscov2.ui.views.HomeView
import com.practica.buscov2.ui.views.LoginView
import com.practica.buscov2.ui.views.OkResetPassword
import com.practica.buscov2.ui.views.RecoverPassword
import com.practica.buscov2.ui.views.RegisterView
import com.practica.buscov2.ui.views.ResetPassword
import com.practica.buscov2.ui.views.StartView


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager() {
    val navController = rememberNavController()
    val tokenViewModel: TokenViewModel = hiltViewModel()
    val loginGoogleViewModel: GoogleLoginViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "Start") {
        composable("Start") {
            val startViewModel: StartViewModel = hiltViewModel()
            val userViewModel: UserViewModel = hiltViewModel()
            StartView(startViewModel, tokenViewModel, userViewModel, navController)
        }

        composable("Login") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val userViewModel: UserViewModel = hiltViewModel()
            LoginView(loginViewModel, userViewModel, loginGoogleViewModel, navController)
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

        //for es para resend password o check email de registro
        composable(
            "CheckEmailView/{userJson}/{forView}",
            arguments = listOf(
                navArgument("userJson") { type = NavType.StringType },
                navArgument("forView") { type = NavType.StringType }
            )
        ) {
            //val email = it.arguments?.getString("email") ?: "su correo."
            val checkEmailViewModel: CheckEmailViewModel = hiltViewModel()

            val userJson = it.arguments?.getString("userJson") ?: ""
            val user = Gson().fromJson(userJson, User::class.java)

            val forView = it.arguments?.getString("forView") ?: "check-email"

            CheckEmailView(checkEmailViewModel, tokenViewModel, navController, user, forView)
        }

        composable("RegisterView") {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            val userViewModel: UserViewModel = hiltViewModel()
            RegisterView(registerViewModel, userViewModel, loginGoogleViewModel, navController)
        }

        composable("Home") {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeView(homeViewModel, loginGoogleViewModel, navController)
        }

        composable("RecoverPassword") {
            val userViewModel: UserViewModel = hiltViewModel()
            val recoverPasswordViewModel: RecoverPasswordViewModel = hiltViewModel()
            RecoverPassword(
                vmUser = userViewModel,
                vmRecover = recoverPasswordViewModel,
                navController = navController
            )
        }

        composable("ResetPassword") {
            val resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
            ResetPassword(resetPasswordViewModel, tokenViewModel, navController)
        }

        composable("OkResetPassword"){
            OkResetPassword(navController)
        }
    }
}