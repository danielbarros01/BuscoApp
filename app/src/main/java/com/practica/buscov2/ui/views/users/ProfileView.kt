package com.practica.buscov2.ui.views.users

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Proposal
import com.practica.buscov2.model.busco.Qualification
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.ItemTabProfile
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonClose
import com.practica.buscov2.ui.components.ButtonWithIcon
import com.practica.buscov2.ui.components.CardJobCompleted
import com.practica.buscov2.ui.components.CardQualificationOfUser
import com.practica.buscov2.ui.components.InsertCircleProfileImage
import com.practica.buscov2.ui.components.ItemsInLazy
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LinkText
import com.practica.buscov2.ui.components.LoaderMaxSize
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.OptionsField
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.StarRatingBar
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.GreenBusco
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.JobsViewModel
import com.practica.buscov2.ui.viewModel.LoadingViewModel
import com.practica.buscov2.ui.viewModel.QualificationsViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.proposals.ProposalsViewModel
import com.practica.buscov2.ui.viewModel.ubication.SearchMapViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import com.practica.buscov2.ui.views.proposals.NoProposals
import com.practica.buscov2.ui.views.proposals.ShowProposals
import com.practica.buscov2.ui.views.util.ActiveLoader.Companion.activeLoaderMax
import kotlinx.coroutines.launch

@Composable
fun ProfileView(
    id: Int,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
    vmProposals: ProposalsViewModel,
    vmQualifications: QualificationsViewModel,
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    searchMapVM: SearchMapViewModel,
    navController: NavHostController
) {
    val token by vmToken.token.collectAsState()
    val user by vmUser.user.collectAsState()
    val userProfile by vmUser.userProfile.collectAsState()

    LaunchedEffect(Unit) {
        token?.let {
            vmUser.getMyProfile(it.token, {
                navController.navigate("Login")
            }) { user ->
                vmUser.changeUser(user)

                if (user.id != id) {
                    // Si yo no soy el usuario, trae el perfil de ese usuario
                    vmUser.getProfile(id, {}) { fetchedUserProfile ->
                        vmUser.changeUserProfile(fetchedUserProfile)
                        fetchedUserProfile.id?.let { userId ->
                            vmProposals.changeUserId(userId)
                        }

                        // Ubicación, después de que userProfile está actualizado
                        fetchedUserProfile.latitude?.let { latitude ->
                            fetchedUserProfile.longitude?.let { longitude ->
                                searchMapVM.getLocation(latitude, longitude) { address ->
                                    address?.let {results ->
                                        searchMapVM.setAddress(results.formatted_address)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Si yo soy el usuario
                    vmUser.changeUserProfile(user)
                    user.id.let { userId ->
                        vmProposals.changeUserId(userId)
                    }

                    // Ubicación para el propio usuario
                    user.latitude?.let { latitude ->
                        user.longitude?.let { longitude ->
                            searchMapVM.getLocation(latitude, longitude) { address ->
                                address?.let {results ->
                                    searchMapVM.setAddress(results.formatted_address)
                                }
                            }
                        }
                    }
                }

                vmQualifications.setWorkerId(id)
                vmJobs.setUserId(id)
            }
        }
    }



    if (userProfile != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProfileV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
                userProfile!!,
                vmProposals,
                vmQualifications,
                vmJobs,
                vmLoading,
                searchMapVM,
                navController
            )
        }
    }
}

@Composable
fun ProfileV(
    modifier: Modifier,
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    userProfile: User,
    vmProposals: ProposalsViewModel,
    vmQualifications: QualificationsViewModel,
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    vmSearchMap: SearchMapViewModel,
    navController: NavHostController
) {
    val isLoading by vmLoading.isLoading
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showPhotoFullScreen by remember { mutableStateOf(false) }

    if (isLoading) {
        LoaderMaxSize()
    }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) { scope ->
        //Mostrar foto en pantalla completa
        if (showPhotoFullScreen) {
            PhotoFullScreen(userProfile) { showPhotoFullScreen = it }
        }

        Scaffold(modifier = modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                TopBarProfile(navController, user, userProfile)
            }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 15.dp)
                //.verticalScroll(rememberScrollState()),
                , horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Imagen de perfil
                InsertCircleProfileImage(
                    image = userProfile.image ?: "",
                    modifier = Modifier
                        .size(160.dp)
                        .shadow(10.dp, shape = CircleShape),
                    onClick = { showPhotoFullScreen = true }
                )

                Space(size = 4.dp)

                BoxUsername(userProfile)

                Space(size = 4.dp)

                if (user.id != userProfile.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ButtonWithIcon(
                            iconId = R.drawable.send,
                            text = "Enviar mensaje",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .width(140.dp)
                                .height(34.dp)
                        ) {
                            //Send message
                            //Abrir el chat con esa persona
                            navController.navigate("Chat/${userProfile.id}")
                        }
                        Space(size = 8.dp)
                        ButtonWithIcon(
                            iconId = R.drawable.briefcase_works,
                            text = "Proponer",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .width(140.dp)
                                .height(34.dp)
                        ) {
                            navController.navigate("Proposals/me/active/${userProfile.id}")
                        }
                    }
                }

                Title(text = "${userProfile.name} ${userProfile.lastname}")

                TabsPages(
                    userProfile,
                    vmProposals,
                    vmQualifications,
                    vmJobs,
                    vmLoading,
                    vmSearchMap,
                    navController
                )

            }
        }
    }
}

@Composable
private fun TabsPages(
    user: User,
    vmProposals: ProposalsViewModel,
    vmQualifications: QualificationsViewModel,
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    vmSearchMap: SearchMapViewModel,
    navController: NavController
) {
    val tabs = if (user.worker == null) ItemTabProfile.pagesUser else ItemTabProfile.pagesWorker

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column(modifier = Modifier.fillMaxSize()) {
        Tabs(tabs, pagerState)
        TabsContent(
            tabs,
            pagerState,
            user,
            vmProposals,
            vmQualifications,
            vmJobs,
            vmLoading,
            vmSearchMap,
            navController
        )
    }
}

@Composable
private fun TabsContent(
    tabs: List<ItemTabProfile>,
    pagerState: PagerState,
    user: User,
    vmProposals: ProposalsViewModel,
    vmQualifications: QualificationsViewModel,
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    vmSearchMap: SearchMapViewModel,
    navController: NavController
) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(
            user,
            vmProposals,
            navController,
            vmQualifications,
            vmJobs,
            vmLoading,
            vmSearchMap
        )
    }
}

@Composable
private fun Tabs(tabs: List<ItemTabProfile>, pagerState: PagerState) {
    val selectedTab = pagerState.currentPage
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(horizontal = if (tabs.size > 2) 0.dp else 60.dp)
            .padding(top = 10.dp)
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = if (tabs.size > 2) 0.dp else 15.dp,
            indicator = { tabPositions ->
                TabRowDefaults.PrimaryIndicator(
                    Modifier
                        .fillMaxWidth()
                        .tabIndicatorOffset(tabPositions[selectedTab]),
                    color = OrangePrincipal,
                    width = tabPositions[selectedTab].width,
                    height = 2.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        //vmProposals.changeStatus(if (tabs[index].title == "Activas") null else true)
                        //vmProposals.refreshProposals()
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = item.title,
                            color = if (index == selectedTab) OrangePrincipal else GrayText
                        )
                    }
                )
            }
        }
    }

}

@Composable
fun WorksCompletedProfile(
    vmJobs: JobsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val jobsPage = vmJobs.jobsCompletedPage.collectAsLazyPagingItems()
    activeLoaderMax(jobsPage, vmLoading)

    if (jobsPage.itemCount == 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No hay trabajos realizados para mostrar", color = GrayText)
        }
    } else {
        ShowJobs(jobsPage, navController)
    }
}

@Composable
fun ShowJobs(jobsPage: LazyPagingItems<Proposal>, navController: NavController) {
    ItemsInLazy(itemsPage = jobsPage) {
        //CARDS
        CardJobCompleted(it) {
            navController.navigate("Proposal/${it.id}")
        }
    }
}

@Composable
fun Qualifications(vmQualifications: QualificationsViewModel, vmLoading: LoadingViewModel) {
    val qualificationsPage = vmQualifications.qualificationsPage.collectAsLazyPagingItems()
    activeLoaderMax(qualificationsPage, vmLoading)

    ShowQualifications(qualificationsPage, vmQualifications)
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowQualifications(
    qualificationsPage: LazyPagingItems<Qualification>,
    vm: QualificationsViewModel
) {
    val quantity by vm.quantity
    val average by vm.rating
    val stars by vm.filterStars

    ItemsInLazy(qualificationsPage, secondViewHeader = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = "$quantity calificaciones", color = GrayPlaceholder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StarRatingBar(5, average, sizeStar = 44.dp, onRatingChanged = {})
                Text(text = "$average/5")
            }

            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Filtrar por:", color = GrayText, fontSize = 18.sp)
                Space(size = 4.dp)
                OptionsField(
                    modifier = Modifier
                        .width(120.dp)
                        .height(54.dp),
                    options = listOf("Todas", "1", "2", "3", "4", "5"),
                    text = if (stars == null) "Todas" else stars.toString(),
                    enabled = true,
                    onChanged = {
                        vm.setFilterStars(it)
                        vm.refreshQualifications()
                    })
            }

            FrequencyTable(vm.ratingFrequencies.value, quantity)
            Space(size = 10.dp)
        }


    }) { qualification ->
        //CARD
        CardQualificationOfUser(qualification.user ?: User(), qualification)
        Space(size = 10.dp)

    }
}

@Composable
fun FrequencyTable(ratingFrequencies: Map<Int, Int>, totalCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        Spacer(modifier = Modifier.height(8.dp))

        ratingFrequencies.toSortedMap().forEach { (rating, count) ->
            val progress = if (totalCount > 0) count.toFloat() / totalCount else 0f

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$rating", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Space(size = 8.dp)

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp),
                    color = GreenBusco,
                    gapSize = 0.dp
                )
                Space(size = 8.dp)

                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Color.LightGray.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(text = "$count", modifier = Modifier.padding(1.dp), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun Information(mapVM: SearchMapViewModel, user: User) {
    val address = mapVM.address.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //Datos ubicacion
        ElementRowInformation(
            imageId = R.drawable.location,
            text = address
        )

        if (user.worker != null) {
            ElementRowInformation(
                iconId = R.drawable.clock,
                text = "${user.worker.yearsExperience} años de experiencia"
            )
            if (user.worker.webPage != null) {
                ElementRowInformation(iconId = R.drawable.link, link = "${user.worker.webPage}")
            }

            Space(size = 4.dp)
            Title(
                text = "${user.worker.professions?.first()?.name}, ${user.worker.title}",
                textAlign = TextAlign.Start,
                size = 20.sp
            )
            Space(size = 4.dp)
            Text(text = user.worker.description ?: "")
        }
    }
}

@Composable
fun ElementRowInformation(
    imageId: Int? = null,
    iconId: Int? = null,
    text: String? = null,
    link: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 5.dp)
    ) {
        if (imageId != null) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "Icono",
                modifier = Modifier.size(36.dp)
            )
        }

        if (iconId != null) {
            Icon(
                painter = painterResource(id = iconId), contentDescription = "Icono",
                modifier = Modifier.size(30.dp), tint = GrayText
            )
        }

        Space(size = 4.dp)

        if (link != null) {
            LinkText(link)
        } else {
            Text(
                text = "$text",
                color = GrayText
            )
        }

    }
}

@Composable
fun Proposals(
    vmProposals: ProposalsViewModel,
    vmLoading: LoadingViewModel,
    navController: NavController
) {
    val proposalsPage = vmProposals.proposalsPage.collectAsLazyPagingItems()
    activeLoaderMax(proposalsPage, vmLoading)

    if (proposalsPage.itemCount == 0) {
        NoProposals()
    } else {
        ShowProposals(proposalsPage, navController)
    }
}

@Composable
fun BoxUsername(user: User) {
    Box(
        modifier = Modifier.background(
            color = GrayPlaceholder.copy(alpha = 0.5f),
            shape = RoundedCornerShape(25.dp),

            )
    ) {
        val username =
            if (user.worker == null) ("@${user.username}")
            else user.worker.professions?.firstOrNull()?.name
                ?: ""

        Text(text = username, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp))
    }
}

@Composable
fun PhotoFullScreen(user: User, setShowPhotoFullScreen: (Boolean) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.89f))
            .zIndex(2f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black),
            contentAlignment = Alignment.TopEnd
        ) {
            ButtonClose(onClick = {
                setShowPhotoFullScreen(false)
            })
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            InsertCircleProfileImage(
                image = user.image ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarProfile(navController: NavHostController, user: User, userProfile: User) {
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = { Text(text = "") },
        navigationIcon = {
            ButtonBack(
                navController = navController,
                size = 48.dp,
                modifier = Modifier.padding(start = 10.dp)
            ) {
                navController.navigate("Home")
            }
        },
        actions = {
            if (user.id == userProfile.id) {
                ButtonWithIcon(
                    iconId = R.drawable.edit,
                    text = "Editar",
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .height(34.dp)
                ) {
                    navController.navigate("EditProfile")
                }
            }
        }
    )
}