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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
import com.practica.buscov2.ui.components.AlertFinishProposal
import com.practica.buscov2.ui.components.AlertQualify
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
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.QualificationsViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.users.ElementRowInformation
import com.practica.buscov2.util.AppUtils.Companion.convertToIsoDate
import com.practica.buscov2.util.AppUtils.Companion.formatNumber

@Composable
fun ProposalView(
    proposalId: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProposal: ProposalViewModel,
    applicationsViewModel: ApplicationsViewModel,
    qualificationsViewModel: QualificationsViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
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
                qualificationsViewModel,
                vmLoading,
                searchMapVM,
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
    qualificationsViewModel: QualificationsViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
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

    val showFinalizeProposal = remember {
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

    val showQualify = remember {
        mutableStateOf(false)
    }

    val commentary by qualificationsViewModel.commentary
    val rating by qualificationsViewModel.rating
    val buttonEnabledQualify by qualificationsViewModel.buttonEnabled

    LaunchedEffect(Unit) {
        vmLoading.withLoading {
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
                    //en proceso de trabajo
                    //Traigo la aplicacion que contiene al trabajador
                    applicationsViewModel.getAcceptedApplication(it.id!!) { worker ->
                        worker?.userId?.let {
                            qualificationsViewModel.setWorkerId(worker.userId)
                        }
                    }
                }

                it.latitude?.let { latitude ->
                    it.longitude?.let { longitude ->
                        searchMapVM.getLocation(latitude, longitude) { address ->
                            address?.let { results ->
                                searchMapVM.setAddress(results.formatted_address)
                            }
                        }
                    }
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
                vmLoading.withLoading {
                    vmProposal.deleteProposal(proposalId, loginToken.token, onError = {
                        showError.value = true
                    }) {
                        //Exito
                        navController.navigate("Proposals")
                    }
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
                vmLoading.withLoading {
                    applicationsViewModel.applyToProposal(
                        proposalId,
                        onError = { error ->
                            vmProposal.setError(error)
                            showError.value = true
                        },
                        onSuccess = {
                            //Ir a mis trabajos
                            navController.navigate("Applications/me")
                        })
                }
            })
    }

    AlertFinishProposal(showFinalizeProposal, onClick = {
        //Finalizar propuesta
        //Abrir alert de calificacion
        token?.let { loginToken ->
            proposal?.id?.let { proposalId ->
                vmLoading.withLoading {
                    vmProposal.finalizeProposal(proposalId, loginToken.token, onError = {
                        showError.value = true
                    }) {
                        //Exito
                        //Poder calificar al trabajador
                        showQualify.value = true
                    }
                }
            }
        }
    })

    AlertQualify(
        showDialog = showQualify,
        name = "${application?.worker?.user?.name} ${application?.worker?.user?.lastname}",
        rating = rating,
        commentary = commentary,
        buttonEnabled = buttonEnabledQualify,
        changeCommentary = { qualificationsViewModel.setCommentary(it) },
        onStars = { qualificationsViewModel.setRating(it) },
        onDismiss = {
            navController.navigate("Proposal/${proposal?.id}")
        },
        onClick = {
            //Realizar creacion de calificacion
            token?.let {
                qualificationsViewModel.createQualification(it.token, onError = {
                    navController.navigate("Proposal/${proposal?.id}")
                }, onSuccess = {
                    navController.navigate("Proposal/${proposal?.id}")
                })
            }

        }
    )

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) {
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {
                Column {
                    if (proposal?.status != true) {
                        //Buttons
                        Row(
                            modifier = Modifier.padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (user.id == userOwnerProposal?.id && proposal?.status != false) {
                                ButtonSquareSmall(color = RedBusco, iconId = R.drawable.delete) {
                                    //Activar verificacion de eliminar
                                    showVerificationDelete.value = true
                                }
                                Space(size = 5.dp)

                                //Boton para editar
                                ButtonSquareSmall(
                                    color = OrangePrincipal,
                                    iconId = R.drawable.edit
                                ) {
                                    navController.navigate("EditProposal/${proposal?.id}")
                                }

                                Space(size = 5.dp)
                            }
                            if (proposal?.status == false && user.id == userOwnerProposal?.id) {
                                Box(modifier = Modifier.width(125.dp)) {
                                    ButtonPrincipal(
                                        text = "Finalizar",
                                        enabled = true,
                                        color = GreenBusco
                                    ) {
                                        //Abrir Alert de finalizar
                                        showFinalizeProposal.value = true
                                    }
                                }
                                Space(size = 5.dp)
                            }
                            if (userOwnerProposal != null) {
                                ButtonPrincipal(
                                    text = if (user.id == userOwnerProposal?.id) "Ver postulantes"
                                    else if (application?.worker?.userId == user.id) "Ir al chat"
                                    else "Aplicar",
                                    enabled = application?.worker?.userId == user.id
                                            || !(proposal?.status == false && user.id != userOwnerProposal?.id)
                                )
                                //verificar si esta en proceso y el usuario no es el dueño
                                {
                                    if (user.id == userOwnerProposal?.id) {
                                        //Ver postulantes
                                        navController.navigate("Applicants/${proposalId}")
                                    } else if (application?.worker?.userId == user.id) {
                                        navController.navigate("Chat/${userOwnerProposal?.id}")
                                    } else {
                                        if(user.worker == null){
                                            navController.navigate("BeWorker")
                                        }else{
                                            //Verificar el tipo de trabajador que buscan
                                            if (user.worker.professions?.firstOrNull()?.id?.equals(
                                                    proposal?.professionId
                                                ) == false
                                            ) {
                                                //Si es distinto mostrar un cuadro de aviso
                                                showAlertDifferentJob.value = true
                                            } else {
                                                //Aplicar e ir a aplicaciones
                                                vmLoading.withLoading {
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

                            if (proposal.status == true) {
                                Finished()
                            }

                            TabsPages(
                                proposal,
                                application,
                                userOwnerProposal,
                                user,
                                searchMapVM,
                                navController
                            )
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
                text = " $${formatNumber(proposal.minBudget.toString())} a $${formatNumber(proposal.maxBudget.toString())}",
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
fun ProposalDescription(proposal: Proposal, application: Application?, user: User?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (application?.worker?.userId == user?.id) {
            HorizontalDivider(color = GreenBusco)
            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = "Estas asignado a este trabajo, comunícate con el autor de esta propuesta.",
                fontWeight = FontWeight.Medium,
                color = GrayText
            )
            HorizontalDivider(color = GreenBusco)
            Space(size = 4.dp)
        }

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
    searchMapVM: SearchMapViewModel,
    navController: NavController?
) {
    val address = searchMapVM.address.value

    val coordinates = LatLng(proposal.latitude!!, proposal.longitude!!)
    val markerState = remember { mutableStateOf(MarkerState(position = coordinates)) }
    val cameraPosition = remember { CameraPosition.fromLatLngZoom(coordinates, 6f) }
    val cameraState = rememberCameraPositionState { position = cameraPosition }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (proposal.status != null && application != null) {
            application.worker?.let {
                //ACA
                DataWorker(it, Qualification(it.averageQualification, it.numberOfQualifications)) {
                    navController?.navigate("Profile/${it.user?.id}")
                }
            }
        }

        ElementRowInformation(
            imageId = R.drawable.location,
            text = address
        )

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraState
                ) {
                    Marker(state = markerState.value)
                }
            }

            Space(size = 8.dp)
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
}

@Composable
private fun TabsPages(
    proposal: Proposal,
    application: Application?,
    userOwner: User?,
    user: User?,
    searchMapVM: SearchMapViewModel,
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
        TabsContent(
            tabs,
            pagerState,
            proposal,
            application,
            userOwner,
            user,
            searchMapVM,
            navController
        )
    }
}


@Composable
private fun TabsContent(
    tabs: List<ItemTabOnlyProposal>,
    pagerState: PagerState,
    proposal: Proposal,
    application: Application? = null,
    userOwner: User?,
    user: User?,
    searchMapVM: SearchMapViewModel,
    navController: NavController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(proposal, application, userOwner, user, navController, searchMapVM)
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
private fun Finished() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.deal),
            contentDescription = "",
            tint = OrangePrincipal
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "Propuesta finalizada",
            color = OrangePrincipal,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
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
