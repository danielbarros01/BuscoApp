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
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.ui.viewModel.confirmation.CheckEmailViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.NewPublicationViewModel
import com.practica.buscov2.ui.viewModel.auth.LoginViewModel
import com.practica.buscov2.ui.viewModel.auth.RecoverPasswordViewModel
import com.practica.buscov2.ui.viewModel.auth.RegisterViewModel
import com.practica.buscov2.ui.viewModel.auth.ResetPasswordViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.professions.ProfessionsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.viewModel.workers.RegisterWorkerViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.views.workers.BeWorkerView
import com.practica.buscov2.ui.views.ChatView
import com.practica.buscov2.ui.views.ConfigurationView
import com.practica.buscov2.ui.views.users.EditUserView
import com.practica.buscov2.ui.views.confirmation.CheckEmailView
import com.practica.buscov2.ui.views.users.CompleteDataView
import com.practica.buscov2.ui.views.HomeView
import com.practica.buscov2.ui.views.JobsView
import com.practica.buscov2.ui.views.proposals.NewPublicationView
import com.practica.buscov2.ui.views.users.ProfileView
import com.practica.buscov2.ui.views.auth.LoginView
import com.practica.buscov2.ui.views.auth.OkResetPassword
import com.practica.buscov2.ui.views.auth.RecoverPassword
import com.practica.buscov2.ui.views.auth.RegisterView
import com.practica.buscov2.ui.views.auth.ResetPassword
import com.practica.buscov2.ui.views.StartView
import com.practica.buscov2.ui.views.proposals.ApplicantsView
import com.practica.buscov2.ui.views.proposals.EditProposalView
import com.practica.buscov2.ui.views.proposals.ProposalView
import com.practica.buscov2.ui.views.proposals.ProposalsView
import com.practica.buscov2.ui.views.workers.RegisterWorkerView


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager() {
    val navController = rememberNavController()
    val tokenViewModel: TokenViewModel = hiltViewModel()
    val loginGoogleViewModel: GoogleLoginViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "Jobs/me") {
        composable("Start") {
            val userViewModel: UserViewModel = hiltViewModel()
            StartView(tokenViewModel, userViewModel, navController)
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
            val completeDataViewModel: CompleteDataViewModel = hiltViewModel()

            CompleteDataView(
                completeDataViewModel,
                navController,
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
            val userViewModel: UserViewModel = hiltViewModel()
            HomeView(
                homeViewModel,
                userViewModel,
                tokenViewModel,
                loginGoogleViewModel,
                navController
            )
        }

        composable("RecoverPassword") {
            val recoverPasswordViewModel: RecoverPasswordViewModel = hiltViewModel()
            RecoverPassword(
                vmRecover = recoverPasswordViewModel,
                navController = navController
            )
        }

        composable("ResetPassword") {
            val resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
            ResetPassword(resetPasswordViewModel, tokenViewModel, navController)
        }

        composable("OkResetPassword") {
            OkResetPassword(navController)
        }

        composable("BeWorker") {
            BeWorkerView(navController)
        }

        composable("RegisterWorker") {
            val registerWorkerViewModel: RegisterWorkerViewModel = hiltViewModel()
            RegisterWorkerView(registerWorkerViewModel, tokenViewModel, navController)
        }

        composable("Chat") {
            ChatView(navController)
        }

        composable("Configuration")
        {
            val userViewModel: UserViewModel = hiltViewModel()

            ConfigurationView(userViewModel, loginGoogleViewModel, tokenViewModel, navController)
        }

        composable(
            "Profile/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val id = it.arguments?.getInt("id") ?: 0

            val userViewModel: UserViewModel = hiltViewModel()
            val proposalsViewModel: ProposalsViewModel = hiltViewModel()

            ProfileView(
                id,
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                proposalsViewModel,
                navController
            )
        }

        composable("EditProfile") {
            val userViewModel: UserViewModel = hiltViewModel()
            val registerWorkerViewModel: RegisterWorkerViewModel = hiltViewModel()
            val completeDataViewModel: CompleteDataViewModel = hiltViewModel()

            EditUserView(
                userViewModel,
                tokenViewModel,
                registerWorkerViewModel,
                completeDataViewModel,
                navController
            )
        }

        composable("New") {
            val userViewModel: UserViewModel = hiltViewModel()
            val professionsViewModel: ProfessionsViewModel = hiltViewModel()
            val newPublicationViewModel: NewPublicationViewModel = hiltViewModel()

            NewPublicationView(
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                professionsViewModel,
                newPublicationViewModel,
                navController
            )
        }

        composable(
            "EditProposal/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val id = it.arguments?.getInt("id") ?: 0
            val userViewModel: UserViewModel = hiltViewModel()
            val professionsViewModel: ProfessionsViewModel = hiltViewModel()
            val newPublicationViewModel: NewPublicationViewModel = hiltViewModel()
            val proposalViewModel: ProposalViewModel = hiltViewModel()

            EditProposalView(
                id,
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                professionsViewModel,
                newPublicationViewModel,
                proposalViewModel,
                navController
            )
        }

        composable("Proposals") {
            val userViewModel: UserViewModel = hiltViewModel()
            val proposalsViewModel: ProposalsViewModel = hiltViewModel()
            ProposalsView(
                vmUser = userViewModel,
                vmGoogle = loginGoogleViewModel,
                vmToken = tokenViewModel,
                vmProposals = proposalsViewModel,
                navController = navController
            )
        }

        composable(
            "Proposal/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val id = it.arguments?.getInt("id") ?: 0
            val userViewModel: UserViewModel = hiltViewModel()
            val proposalViewModel: ProposalViewModel = hiltViewModel()
            val applicantsViewModel: ApplicationsViewModel = hiltViewModel()

            ProposalView(
                id,
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                proposalViewModel,
                applicantsViewModel,
                navController
            )
        }


        composable(
            "Applicants/{proposalId}",
            arguments = listOf(navArgument("proposalId") { type = NavType.IntType })
        ) {
            val proposalId = it.arguments?.getInt("proposalId") ?: 0
            val userViewModel: UserViewModel = hiltViewModel()
            val applicantsViewModel: ApplicationsViewModel = hiltViewModel()
            ApplicantsView(
                proposalId,
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                applicantsViewModel,
                navController
            )
        }

        composable("Jobs/me") {
            val userViewModel: UserViewModel = hiltViewModel()
            val proposalsViewModel: ProposalsViewModel = hiltViewModel()
            val jobsViewModel: JobsViewModel = hiltViewModel()

            JobsView(
                userViewModel,
                loginGoogleViewModel,
                tokenViewModel,
                proposalsViewModel,
                jobsViewModel,
                navController
            )
        }
    }
}