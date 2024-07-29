package com.practica.buscov2.ui.views

import android.Manifest
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.SimpleUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.navigation.RoutesDrawer
import com.practica.buscov2.ui.components.AlertChangeUbication
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonMenu
import com.practica.buscov2.ui.components.ButtonPrincipal
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
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.theme.Rubik
import com.practica.buscov2.ui.theme.ShadowColor
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.SearchViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.professions.ProfessionsViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.viewModel.users.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.util.AppUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeView(
    homeVm: HomeViewModel,
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmCompleteData: CompleteDataViewModel,
    vmProfessions: ProfessionsViewModel,
    vmSearch: SearchViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()

    //Ejecuto una unica vez
    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) { user ->
                vmCompleteData.onDateChangedInitializedData(user)
                vmSearch.onUbicationChange(SimpleUbication(user.country, user.province, user.department, user.city))
            }
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
                vmCompleteData,
                vmProfessions,
                vmSearch,
                navController,
                user!!
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    modifier: Modifier,
    homeVm: HomeViewModel,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmCompleteData: CompleteDataViewModel,
    vmProfessions: ProfessionsViewModel,
    vmSearch: SearchViewModel,
    navController: NavHostController,
    user: User
) {
    val navigationRoutes = RoutesBottom.allRoutes

    val isLoading by homeVm.isLoading.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var isSearchWork by remember {
        mutableStateOf(false)
    }

    val changeUbication = remember {
        mutableStateOf(false)
    }

    if (isLoading) {
        LoaderMaxSize()
    }

    AlertChangeUbication(
        changeUbication,
        vmCompleteData
    ) { pais, provincia, departamento, localidad ->
        vmSearch.onUbicationChange(SimpleUbication(pais, provincia, departamento, localidad))
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

                        } else {
                            navController.navigate("BeWorker")
                        }
                    }, onClickClient = {
                        isSearchWork = false
                    })
                }

                //Buscar
                Column {
                    ButtonUbication(
                        ubication = SimpleUbication(
                            country = vmCompleteData.pais.value,
                            province = vmCompleteData.provincia.value,
                            department = vmCompleteData.departamento.value,
                            city = vmCompleteData.localidad.value
                        ), modifier = Modifier.offset(x = -(15.dp))
                    ) {
                        changeUbication.value = true
                    }

                    if (isSearchWork) {
                        val proposalsPage = homeVm.proposalsPage.collectAsLazyPagingItems()
                        activeLoaderMax(proposalsPage, homeVm)
                        ButtonPrincipal(
                            text = "Buscar",
                            enabled = true,
                            modifier = Modifier.padding(bottom = 15.dp)
                        ) {
                        }

                        ShowProposals(proposalsPage, navController)
                    } else {
                        val workersPage = homeVm.workersPage.collectAsLazyPagingItems()
                        activeLoaderMax(workersPage, homeVm)


                        SearchSection(vmProfessions, onQueryChange = { query ->
                            vmSearch.onQueryChange(query)
                        }, onSearch = {
                            //pasar en la ruta el query
                            navController.navigate("Search")
                        })

                        Space(size = 10.dp)
                        ShowWorkers(workersPage, navController)
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
fun ShowWorkers(workersPage: LazyPagingItems<User>, navController: NavController) {
    ItemsInLazy(workersPage) {
        CardWorkerRecommendation(
            modifier = Modifier.padding(vertical = 10.dp),
            user = it,
            qualification = 99
        ) {
            // Ir al perfil del usuario
            navController.navigate("Profile/${it.id}")
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

fun <T : Any> activeLoaderMax(
    itemsPage: LazyPagingItems<T>,
    vmHomeViewModel: HomeViewModel
) {
    val loadState = itemsPage.loadState
    val isLoading = loadState.refresh is LoadState.Loading || loadState.prepend is LoadState.Loading
    vmHomeViewModel.setLoading(isLoading)
}