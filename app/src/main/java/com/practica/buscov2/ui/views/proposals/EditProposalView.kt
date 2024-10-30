package com.practica.buscov2.ui.views.proposals

import com.practica.buscov2.ui.views.images.SelectImageFromGallery
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.practica.buscov2.model.busco.SimpleUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.AlertSelectPicture
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.ButtonUbication
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.NewPublicationViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.professions.ProfessionsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.images.UseCamera

@Composable
fun EditProposalView(
    proposalId: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProfession: ProfessionsViewModel,
    vmNewPublication: NewPublicationViewModel,
    vmProposal: ProposalViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()
    val proposal by vmProposal.proposal

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {
                vmLoading.withLoading {

                    //Traigo la propuesta
                    vmProposal.getProposal(proposalId, {}) { p ->
                        //Cambio los valores del vm
                        vmProposal.changeProposal(p)
                        vmNewPublication.setId(p.id!!)
                        vmNewPublication.setData(
                            title = p.title ?: "",
                            description = p.description ?: "",
                            requirements = p.requirements ?: ""
                        )
                        vmNewPublication.setBudget(
                            p.minBudget.toString(),
                            p.maxBudget.toString()
                        )

                        //Debo traer la profesion
                        vmProfession.getProfession(p.professionId!!, {}) { profession ->
                            vmNewPublication.setProfession(profession)
                        }

                        vmNewPublication.setImage(Uri.parse(p.image))
                    }
                }
            }
        }
    }

    if (user != null && proposal != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            EditPublication(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                token,
                vmProfession,
                vmNewPublication,
                vmProposal,
                vmLoading,
                navController
            )
        }
    }
}

@Composable
fun EditPublication(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    token: LoginToken?,
    vmProfession: ProfessionsViewModel,
    vmNewPublication: NewPublicationViewModel,
    vmProposal: ProposalViewModel,
    vmLoading: LoadingViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isLoading by vmLoading.isLoading
    val error = vmNewPublication.error
    val showError = remember { mutableStateOf(false) }

    val openAlertSelectImage = remember { mutableStateOf(false) }
    val openCamera = remember { mutableStateOf(false) }
    val openGallery = remember { mutableStateOf(false) }

    val uriPicture = vmNewPublication.image
    val newUriPicture = remember {
        mutableStateOf(uriPicture.value)
    }

    val buttonEnabled by vmNewPublication.buttonEnabled

    //Mostrar error
    AlertError(showDialog = showError, error.value.title, error.value.message)
    //Mostrar loader
    if (isLoading) {
        LoaderMaxSize()
    }
    //Seleccionar forma de elegir imagen
    AlertSelectPicture(
        showDialog = openAlertSelectImage,
        openCamera = {
            openCamera.value = true
            openAlertSelectImage.value = false
        },
        openGallery = {
            openGallery.value = true
            openAlertSelectImage.value = false
        }
    )

    if (openCamera.value) {
        UseCamera(onBack = { openCamera.value = false }) { uri ->
            openCamera.value = false
            newUriPicture.value = uri
        }
    }

    if (openGallery.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f)
        ) {
            SelectImageFromGallery(onGalleryClosed = {
                openGallery.value = false
            }) { uri ->
                newUriPicture.value = uri
                openGallery.value = false
            }
        }
    }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) { scope ->
        Scaffold(modifier = modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                Column {
                    TopBar(title = "Editar Propuesta", scope = scope, drawerState = drawerState)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangePrincipal)
                    ) {
                        ButtonUbication(
                            "dasdasdasd",
                            textColor = Color.White
                        )
                    }
                }
            }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 0.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleNewPublication()
                Space(size = 5.dp)
                DataProposal(
                    vmProfession,
                    vmNewPublication,
                    token,
                    showError,
                    openAlertSelectImage,
                    newUriPicture
                ) {
                    ButtonPrincipal(text = "Editar", enabled = buttonEnabled) {
                        //Si tengo el token
                        token?.let { loginToken ->
                            //Si propuesta no es null
                            //Editamos
                            vmLoading.withLoading {
                                vmProposal.editProposal(
                                    context,
                                    newUriPicture.value,
                                    token = loginToken.token,
                                    {
                                        //MOSTRAR ERROR
                                        showError.value = true
                                    }) {
                                    //Volver a propuesta ya editada
                                    navController.popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
