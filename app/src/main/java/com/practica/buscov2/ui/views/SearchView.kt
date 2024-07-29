package com.practica.buscov2.ui.views

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.SimpleUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.AlertChangeUbication
import com.practica.buscov2.ui.components.AlertFilters
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonUbication
import com.practica.buscov2.ui.components.CardJob
import com.practica.buscov2.ui.components.CardWorker
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.TopBar
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.SearchViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ApplicationsViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.proposals.NewPublication

@Composable
fun SearchView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmSearch: SearchViewModel,
    vmCompleteData: CompleteDataViewModel,
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
        }
    }

    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            SearchV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                vmSearch,
                vmCompleteData,
                navController
            )
        }
    }
}

@Composable
fun SearchV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    vmSearch: SearchViewModel,
    vmCompleteData: CompleteDataViewModel,
    navController: NavHostController
) {
    val isLoading by vmSearch.isLoading
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

    //Mostrar loader
    if (isLoading) {
        LoaderMaxSize()
    }

    AlertFilters(showDialog = showAlertFilters, categories = categories) { stars, category ->
        //Poner estos valores en el viewModel
        vmSearch.setStars(stars)
        vmSearch.setCategory(category)

        vmSearch.refreshWorkers()
    }

    AlertChangeUbication(
        changeUbication,
        vmCompleteData,
        onChange = { pais, provincia, departamento, localidad ->
            vmSearch.onUbicationChange(SimpleUbication(pais, provincia, departamento, localidad))
            vmSearch.refreshWorkers()
        }
    )

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
                    TopBar(title = "Buscando $query", scope = scope, drawerState = drawerState)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangePrincipal)
                    ) {
                        ButtonUbication(
                            ubication = SimpleUbication(
                                country = vmSearch.ubication.value.country,
                                province = vmSearch.ubication.value.province,
                                department = vmSearch.ubication.value.department,
                                city = vmSearch.ubication.value.city
                            ),
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
                        Text(text = "", color = OrangePrincipal)
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
                val workersPage = vmSearch.workersPage.collectAsLazyPagingItems()

                ItemsInLazy(workersPage, secondViewHeader = {}) { worker ->
                    CardWorker(
                        modifier = Modifier
                            .height(150.dp)
                            .padding(vertical = 10.dp),
                        worker = worker,
                        rating = Qualification(worker.averageQualification),
                        onClick = {
                            navController.navigate("Profile/${worker.userId}")
                        }
                    )
                }
            }
        }
    }
}
