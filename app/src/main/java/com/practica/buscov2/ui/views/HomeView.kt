package com.practica.buscov2.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.data.dataStore.StoreUbication
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.model.busco.Worker
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.navigation.RoutesDrawer
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonMenu
import com.practica.buscov2.ui.components.ButtonUbication
import com.practica.buscov2.ui.components.CardProposalRecommendation
import com.practica.buscov2.ui.components.CardWorkerRecommendation
import com.practica.buscov2.ui.components.InsertCircleProfileImage
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.SearchField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TriangleShape
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.Rubik
import com.practica.buscov2.ui.theme.ShadowColor
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.SearchViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.professions.ProfessionsViewModel
import com.practica.buscov2.ui.viewModel.ubication.MapViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.maps.MapViewUI
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeView(
    homeVm: HomeViewModel,
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmCompleteData: CompleteDataViewModel,
    vmProfessions: ProfessionsViewModel,
    vmSearch: SearchViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) {}
            homeVm.setToken(it.token)
            homeVm.refreshWorkers()
        }
    }


    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Home(
                Modifier.align(Alignment.Center),
                homeVm,
                vmUser,
                vmGoogle,
                vmProfessions,
                vmSearch,
                vmLoading,
                searchMapVM,
                mapVM,
                navController,
                user!!
            )
        }
    }
}

@Composable
fun Home(
    modifier: Modifier,
    homeVm: HomeViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmProfessions: ProfessionsViewModel,
    vmSearch: SearchViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController,
    user: User
) {
    var typeSearch by remember {
        mutableStateOf("workers") // workers or proposals
    }

    val navigationRoutes = RoutesBottom.allRoutes

    val isLoading by vmLoading.isLoading
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val ubicationDataStore = StoreUbication(context)
    val ubication = ubicationDataStore.getUbicationFlow().collectAsState(initial = null)
    val address = mapVM.address.value
    val coordinates = searchMapVM.placeCoordinates.value
    var setUbicationStart by remember { mutableStateOf(false) }

    LaunchedEffect(ubication.value) {
        val location = ubication.value ?: LatLng(user.latitude!!, user.longitude!!)

        searchMapVM.setLocation(location.latitude, location.longitude)

        searchMapVM.getLocation(location.latitude, location.longitude) { address ->
            address?.let {
                mapVM.setAddress(it.formatted_address)
            }
        }

        homeVm.setUbication(location.latitude, location.longitude)

        setUbicationStart = true
    }


    //El usuario que busca es trabajador y esta buscando propuestas
    var isSearchWork by remember {
        mutableStateOf(false)
    }

    val changeUbication = remember {
        mutableStateOf(false)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    if (changeUbication.value) {
        MapViewUI(
            coordinates.latitude,
            coordinates.longitude,
            searchMapVM,
            mapVM,
            navController,
            actionClose = {
                changeUbication.value = false //Close Map
            }) {
            //Cambiar ubicacion en la memoria del celular
            scope.launch {
                ubicationDataStore.saveUbication(it)
                homeVm.setUbication(it.latitude, it.longitude)
                changeUbication.value = false //close map

                //Actualizar recomendaciones
                if (isSearchWork) {
                    homeVm.refreshProposals()
                } else {
                    homeVm.refreshWorkers()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuNavigation(
                vmUser = vmUser,
                vmGoogle = vmGoogle,
                user = user,
                navController = navController,
                routes = RoutesDrawer.allRoutes
            )
        }
    ) {
        //Screen content
        Scaffold(modifier = Modifier
            .fillMaxSize(),
            bottomBar = {

                BottomNav(navController, navigationRoutes)

            }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(15.dp)
            ) {
                //Top Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ButtonMenu(modifier = Modifier) {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }

                    Box(modifier = Modifier.size(50.dp)) {
                        InsertCircleProfileImage(
                            image = user.image ?: "",
                            modifier = Modifier
                        ) {
                            navController.navigate("Profile/${user.id}")
                        }
                    }
                }

                //Titulo con buscar trabajador o trabajos
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((-15).dp)
                )
                {
                    Title()
                    TriangleShape(modifier.size(32.dp), color = OrangePrincipal)
                    ButtonsChangeType(onClickWorker = {
                        if (user.worker != null) {
                            isSearchWork = true
                            typeSearch = "proposals"

                        } else {
                            navController.navigate("BeWorker")
                        }
                    }, onClickClient = {
                        isSearchWork = false
                        typeSearch = "workers"
                    })
                }

                //Buscar
                Column {
                    ButtonUbication(
                        if (setUbicationStart) {
                            address
                        } else "Cargando ubicación...",
                        modifier = Modifier.offset(x = -(15.dp))
                    ) {
                        changeUbication.value = true //Open Map
                    }

                    if (setUbicationStart) {
                        if (isSearchWork) {
                            val proposalsPage = homeVm.proposalsPage.collectAsLazyPagingItems()
                            activeLoaderMax(proposalsPage, vmLoading)

                            SearchSection(vmProfessions, onQueryChange = { query ->
                                vmSearch.onQueryChange(query)
                            }, onSearch = {
                                navController.navigate("Search/${typeSearch}")
                            })

                            Space(size = 10.dp)
                            ShowProposals(proposalsPage, navController)
                        } else {
                            val workersPage: LazyPagingItems<Worker> =
                                homeVm.workersPage.collectAsLazyPagingItems()
                            activeLoaderMax(workersPage, vmLoading)


                            SearchSection(vmProfessions, onQueryChange = { query ->
                                vmSearch.onQueryChange(query)
                            }, onSearch = {
                                navController.navigate("Search/${typeSearch}")
                            })

                            Space(size = 10.dp)
                            ShowWorkers(workersPage, navController)
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun SearchSection(
    vmProfessions: ProfessionsViewModel,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val professions by vmProfessions.professions

    SearchField(
        onQueryChange = {
            vmProfessions.getProfessions(it)
            onQueryChange(it)
        },
        onSearch = { onSearch() },
        content = {
            //Mostrar lista de profesiones
            Column(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                ) {
                    professions.forEach { profession ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp)
                                .clickable {
                                    onQueryChange(profession.name)
                                    onSearch()
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profession.name,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize()
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    )
}

@Composable
fun ShowWorkers(workersPage: LazyPagingItems<Worker>, navController: NavController) {
    ItemsInLazy(workersPage) {
        CardWorkerRecommendation(
            modifier = Modifier
                .padding(vertical = 10.dp),
            worker = it,
            qualification = it.averageQualification?.roundToInt() ?: 0
        ) {
            // Ir al perfil del usuario
            navController.navigate("Profile/${it.userId}")
        }
    }
}

@Composable
fun ShowProposals(proposalsPage: LazyPagingItems<Proposal>, navController: NavController) {
    ItemsInLazy(proposalsPage) {
        CardProposalRecommendation(
            modifier = Modifier.padding(vertical = 10.dp),
            proposal = it
        ) {
            //Ir a la propuesta
            navController.navigate("Proposal/${it.id}")
        }
    }
}

@Composable
fun ButtonsChangeType(
    onClickWorker: () -> Unit,
    onClickClient: () -> Unit
) {
    var activeWorker by remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(top = 16.dp)) {
        Button(
            onClick = {
                activeWorker = true
                onClickWorker()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (activeWorker) OrangePrincipal else GrayField
            ),
            shape = RoundedCornerShape(
                bottomStart = 15.dp,
                topStart = 15.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            ),
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "Trabajar", modifier = Modifier.padding(vertical = 4.dp))
        }
        Space(size = 0.5.dp)
        Button(
            onClick = {
                activeWorker = false
                onClickClient()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!activeWorker) OrangePrincipal else GrayField
            ),
            shape = RoundedCornerShape(
                bottomStart = 0.dp,
                topStart = 0.dp,
                topEnd = 15.dp,
                bottomEnd = 15.dp
            ),
            modifier = Modifier.width(120.dp)
        ) {
            Text(text = "Trabajador", modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun Title() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LetterTitle('B')
        OutlinedTitle("usco")
    }
}

@Composable
fun LetterTitle(char: Char) {
    Text(
        text = char.toString(),
        color = OrangePrincipal,
        fontSize = 70.sp,
        fontWeight = FontWeight.ExtraBold,
        style = TextStyle.Default.copy(
            fontFamily = Rubik,
            shadow = Shadow(
                ShadowColor,
                offset = Offset(7f, 10f),
                blurRadius = 18f
            ),
        )
    )
}

@Composable
fun OutlinedTitle(text: String) {
    Box {
        Text(
            text = text,
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle.Default.copy(
                fontFamily = Rubik,
                shadow = Shadow(
                    ShadowColor,
                    offset = Offset(7f, 10f),
                    blurRadius = 18f
                ),
            )
        )


        Text(
            text = text,
            color = OrangePrincipal,
            style = TextStyle.Default.copy(
                fontFamily = Rubik,
                fontSize = 64.sp,
                drawStyle = Stroke(width = 6f, join = StrokeJoin.Round),
                fontWeight = FontWeight.ExtraBold,
            )
        )
    }
}