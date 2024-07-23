package com.practica.buscov2.ui.views.proposals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Application
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.model.busco.auth.LoginToken
import com.practica.buscov2.navigation.ItemTabOnlyProposal
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertDifferentJob
import com.practica.buscov2.ui.components.AlertError
import com.practica.buscov2.ui.components.AlertVerificationDelete
import com.practica.buscov2.ui.components.ButtonSquareSmall
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.CardWorker
import com.practica.buscov2.ui.components.InfiniteRotationIcon
import com.practica.buscov2.ui.components.InsertAsyncImage
import com.practica.buscov2.ui.components.InsertCircleProfileImage
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TabsComponent
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.BlueLink
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.RedBusco
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.util.AppUtils.Companion.convertToIsoDate

@Composable
fun ProposalView(
    proposalId: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProposal: ProposalViewModel,
    applicationsViewModel: ApplicationsViewModel,
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

            applicationsViewModel.setToken(it.token)
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProposalV(
                Modifier.align(Alignment.Center),
                proposalId,
                vmUser,
                vmGoogle,
                user!!,
                vmProposal,
                token,
                applicationsViewModel,
                navController
            )
        }
    }
}

@Composable
fun ProposalV(
    modifier: Modifier,
    proposalId: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    vmProposal: ProposalViewModel,
    token: LoginToken?,
    applicationsViewModel: ApplicationsViewModel,
    navController: NavHostController
) {
    val error by vmProposal.error
    val showError = remember {
        mutableStateOf(false)
    }
    var showErrorClient by remember {
        mutableStateOf(false)
    }
    var showSuccess by remember {
        mutableStateOf(false)
    }

    val proposal by vmProposal.proposal
    val userOwnerProposal by vmProposal.userOwner

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val showVerificationDelete = remember {
        mutableStateOf(false)
    }

    val application by applicationsViewModel.applicant

    val showAlertDifferentJob = remember {
        mutableStateOf(false)
    }


    LaunchedEffect(Unit) {
        vmProposal.getProposal(proposalId, onError = {
            // Manejo de error
            showErrorClient = true
        }) {
            // Manejo de éxito
            showSuccess = true

            //Traigo al usuario que creo la propuesta
            it.userId?.let { userId ->
                vmUser.getProfile(userId, {}) { user ->
                    vmProposal.changeUserOwner(user)
                }
            }

            if (it.status != null) {
                if (!it.status) {
                    //en proceso de trabajo
                    //Traigo la aplicacion que contiene al trabajador
                    applicationsViewModel.getAcceptedApplication(it.id!!)
                }
            }
        }
    }


    AlertError(showError, "Error", message = error.message)

    AlertVerificationDelete(
        showVerificationDelete,
        "¿Desea eliminar esta propuesta?",
        message = "Una vez eliminada no podras recuperarla"
    ) {
        token?.let { loginToken ->
            proposal?.id?.let { proposalId ->
                //Eliminar propuesta
                vmProposal.deleteProposal(proposalId, loginToken.token, onError = {
                    showError.value = true
                }) {
                    //Exito
                    navController.navigate("Proposals")
                }
            }
        }
    }


    proposal?.let {
        AlertDifferentJob(
            showAlertDifferentJob,
            it,
            user,
            onClick = {
                //Aplicar e ir a aplicaciones
                applicationsViewModel.applyToProposal(
                    proposalId,
                    onError = {
                        vmProposal.setError(it)
                        showError.value = true
                    },
                    onSuccess = {
                        //Ir a mis trabajos
                        navController.navigate("Jobs/me")
                    })
            })
    }





    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                Column {
                    Row(
                        modifier = Modifier.padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.id == userOwnerProposal?.id) {
                            ButtonSquareSmall(color = RedBusco, iconId = R.drawable.delete) {
                                //Activar verificacion de eliminar
                                showVerificationDelete.value = true
                            }
                            Space(size = 5.dp)

                            //Boton para editar
                            ButtonSquareSmall(color = OrangePrincipal, iconId = R.drawable.edit) {
                                navController.navigate("EditProposal/${proposal?.id}")
                            }

                            Space(size = 5.dp)
                        }
                        if (userOwnerProposal != null) {
                            ButtonPrincipal(
                                text = if (user.id == userOwnerProposal?.id) "Ver postulantes" else "Aplicar",
                                enabled = true
                            ) {
                                if (user.id == userOwnerProposal?.id) {
                                    //Ver postulantes
                                    navController.navigate("Applicants/${proposalId}")
                                } else {
                                    //Verificar el tipo de trabajador que buscan
                                    if (user.worker?.workersProfessions?.firstOrNull()?.professionId?.equals(
                                            proposal?.professionId
                                        ) == false
                                    ) {
                                        //Si es distinto mostrar un cuadro de aviso
                                        showAlertDifferentJob.value = true
                                    } else {
                                        //Aplicar e ir a aplicaciones
                                        applicationsViewModel.applyToProposal(
                                            proposalId,
                                            onError = {
                                                vmProposal.setError(it)
                                                showError.value = true
                                            },
                                            onSuccess = {
                                                //Ir a mis trabajos
                                                navController.navigate("Jobs/me")
                                            })
                                    }
                                }
                            }
                        }
                    }
                    BottomNav(navController, RoutesBottom.allRoutes)
                }

            }) {
            Column(
                modifier = modifier
                    .padding(it)
                    .fillMaxSize()
            ) {
                if (showErrorClient) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = error.message, color = GrayText)
                    }
                }

                if (showSuccess) {
                    proposal?.let { proposal ->
                        Header(proposal, navController)
                        Column(
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    start = 15.dp,
                                    end = 15.dp
                                )
                        ) {
                            PriceAndDate(proposal)
                            Title(
                                text = proposal.title ?: "",
                                textAlign = TextAlign.Start,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (proposal.status == false) {
                                Working()
                            }

                            TabsPages(proposal, application, userOwnerProposal, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header(proposal: Proposal, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        InsertAsyncImage(
            image = proposal.image ?: "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            defaultImg = R.drawable.camerapicnull
        )
        ButtonBack(
            modifier = Modifier.padding(15.dp),
            navController = navController
        )
    }
}

@Composable
fun PriceAndDate(proposal: Proposal) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Presupuesto:",
                color = GrayField,
                fontSize = 12.sp
            )
            Text(
                text = " $${proposal.minBudget.toString()} a $${proposal.maxBudget.toString()}",
                fontSize = 14.sp, color = GrayField, fontWeight = FontWeight.Medium,
                modifier = Modifier.offset(y = -(5).dp)
            )
        }
        Text(
            text = convertToIsoDate(proposal.date.toString(), "yyyy-MM-dd'T'HH:mm:ss"),
            color = OrangePrincipal
        )
    }
}

@Composable
fun ProposalDescription(proposal: Proposal, user: User?, navController: NavController?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = proposal.description ?: "")

        Text(
            text = "Requerimientos:",
            color = OrangePrincipal,
            modifier = Modifier.padding(top = 10.dp)
        )

        Text(text = proposal.requirements ?: "")
    }
}

@Composable
fun ProposalMoreInfo(
    proposal: Proposal,
    application: Application? = null,
    user: User?,
    navController: NavController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (proposal.status == false && application != null) {
            application.worker?.let {
                DataWorker(it, Qualification(5f, 20)) {
                    navController?.navigate("Profile/${it.user?.id}")
                }
            }
        }

        Text(text = "Acerca del cliente:", color = OrangePrincipal)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .padding(end = 5.dp)
            ) {
                InsertCircleProfileImage(
                    image = user?.image ?: "",
                    modifier = Modifier.aspectRatio(1f)
                )
            }
            Text(
                text = "${user?.name} ${user?.lastname}",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        Text(text = user?.worker?.description ?: "")
        Text(
            text = "Ver Perfil",
            color = BlueLink,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                navController?.navigate("Profile/${user?.id}")
            })
    }
}

@Composable
private fun TabsPages(
    proposal: Proposal,
    application: Application?,
    user: User?,
    navController: NavController
) {
    val tabs = ItemTabOnlyProposal.pagesProposal
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column {
        TabsComponent(
            tabs,
            pagerState,
            modifier = Modifier.padding(horizontal = 40.dp, vertical = 5.dp),
            fontSize = 14.sp
        )
        TabsContent(tabs, pagerState, proposal, application, user, navController)
    }
}


@Composable
private fun TabsContent(
    tabs: List<ItemTabOnlyProposal>,
    pagerState: PagerState,
    proposal: Proposal,
    application: Application? = null,
    user: User?,
    navController: NavController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(proposal, application, user, navController)
    }
}

@Composable
private fun Working() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        InfiniteRotationIcon(modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "En proceso de trabajo", color = GreenBusco, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DataWorker(worker: Worker, qualification: Qualification, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(text = "Trabajador asignado:", color = OrangePrincipal)
        Space(size = 5.dp)
        CardWorker(
            worker = worker,
            rating = qualification,
            modifier = Modifier.height(140.dp),
            onClickName = { onClick() },
            onClick = { onClick() })
    }
}
