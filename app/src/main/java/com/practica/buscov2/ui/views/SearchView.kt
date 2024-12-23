package com.practica.buscov2.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.maps.model.LatLng
import com.practica.buscov2.R
import com.practica.buscov2.data.dataStore.StoreUbication
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertFilters
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonUbication
import com.practica.buscov2.ui.components.CardProposal
import com.practica.buscov2.ui.components.CardWorker
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.SearchViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.ubication.MapViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.maps.MapViewUI
import com.practica.buscov2.ui.views.util.ActiveLoader
import com.practica.buscov2.util.AppUtils
import com.practica.buscov2.util.AppUtils.Companion.formatNumber
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchView(
    typeSearch: String, // workers or proposals
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmSearch: SearchViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
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

            vmSearch.setToken(it.token)
            vmSearch.resetValues()
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            SearchV(
                Modifier.align(Alignment.Center),
                typeSearch,
                vmUser,
                vmGoogle,
                user!!,
                vmSearch,
                vmLoading,
                searchMapVM,
                mapVM,
                navController
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchV(
    modifier: Modifier,
    search: String,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    vmSearch: SearchViewModel,
    vmLoading:LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    mapVM: MapViewModel,
    navController: NavHostController
) {
    val isLoading by vmLoading.isLoading
    val query by vmSearch.query
    val changeUbication = remember {
        mutableStateOf(false)
    }
    val showAlertFilters = remember {
        mutableStateOf(false)
    }

    val categories by vmSearch.categories

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    //Ubication
    val scope = rememberCoroutineScope()
    val ubicationDataStore = StoreUbication(context)
    val ubication = ubicationDataStore.getUbicationFlow().collectAsState(initial = null)
    val address = mapVM.address.value
    val coordinates = searchMapVM.placeCoordinates.value
    var setUbicationStart by remember { mutableStateOf(false) }

    val totalRecords by vmSearch.totalRecords.collectAsState()

    LaunchedEffect(ubication.value) {
        val location = ubication.value ?: LatLng(user.latitude!!, user.longitude!!)

        searchMapVM.setLocation(location.latitude, location.longitude)

        searchMapVM.getLocation(location.latitude, location.longitude) { address ->
            address?.let {
                mapVM.setAddress(it.formatted_address)
            }
        }

        //Para la busqueda data source
        vmSearch.setUbication(location.latitude, location.longitude)

        setUbicationStart = true
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
                vmSearch.setUbication(it.latitude, it.longitude)
                changeUbication.value = false //close map

                //Actualizar recomendaciones o trabajadores con refresh ***
                if (search == "workers") {
                    vmSearch.refreshWorkers()
                }else{
                    vmSearch.refreshProposals()
                }
            }
        }
    }

    //Mostrar loader
    if (isLoading) {
        LoaderMaxSize()
    }

    AlertFilters(
        showDialog = showAlertFilters,
        categories = categories,
        filterQualificationView = search == "workers" //si busco trabajadores puedo filtrar por calificacion
    ) { stars, category ->
        //Poner estos valores en el viewModel
        vmSearch.setStars(stars)
        vmSearch.setCategory(category)

        if( search == "workers"){
            vmSearch.refreshWorkers()
        }else{
            vmSearch.refreshProposals()
        }
    }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) {
        Scaffold(modifier = modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                Column {
                    TopBar(title = "Buscando $query", scope = scope, drawerState = drawerState)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangePrincipal)
                    ) {
                        ButtonUbication(
                            address,
                            textColor = Color.White
                        ) {
                            changeUbication.value = true
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(text = "$totalRecords resultados", color = OrangePrincipal)
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Filtrar", color = OrangePrincipal)
                            IconButton(onClick = {
                                //Abrir alert para filtrar
                                showAlertFilters.value = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.filter),
                                    contentDescription = "Filtrar",
                                    tint = OrangePrincipal
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = OrangePrincipal)
                }
            }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (search == "workers") {
                    SearchWorkers(vmSearch, vmLoading,navController)
                } else {
                    SearchProposals(vmSearch,vmLoading, navController)
                }
            }
        }
    }
}

@Composable
fun SearchWorkers(vmSearch: SearchViewModel,vmLoading: LoadingViewModel, navController: NavController) {
    val workersPage = vmSearch.workersPage.collectAsLazyPagingItems()
    ActiveLoader.activeLoaderMax(workersPage, vmLoading)

    if(workersPage.itemCount == 0){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No se encontraron trabajadores")
        }
    }

    ItemsInLazy(workersPage, secondViewHeader = {}) { worker ->
        CardWorker(
            modifier = Modifier
                .height(150.dp)
                .padding(vertical = 10.dp),
            worker = worker,
            rating = Qualification(worker.averageQualification, worker.numberOfQualifications),
            onClick = {
                navController.navigate("Profile/${worker.userId}")
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchProposals(vmSearch: SearchViewModel,vmLoading: LoadingViewModel, navController: NavController) {
    val proposalsPage = vmSearch.proposalsPage.collectAsLazyPagingItems()
    ActiveLoader.activeLoaderMax(proposalsPage, vmLoading)

    if(proposalsPage.itemCount == 0){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Sin resultados")
        }
    }

    ItemsInLazy(proposalsPage, secondViewHeader = {}) {
        CardProposal(
            image = it.image ?: "",
            title = it.title ?: "",
            price = "$${formatNumber(it.minBudget.toString())} a $${formatNumber(it.maxBudget.toString())}",
            date = AppUtils.formatDateCard("${it.date}"),
        ) {
           navController.navigate("Proposal/${it.id}")
        }
    }
}
