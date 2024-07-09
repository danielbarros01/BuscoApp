package com.practica.buscov2.ui.views.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.ItemTabProfile
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.ButtonClose
import com.practica.buscov2.ui.components.ButtonWithIcon
import com.practica.buscov2.ui.components.InsertCircleProfileImage
import com.practica.buscov2.ui.components.LateralMenu
import com.practica.buscov2.ui.components.LinkText
import com.practica.buscov2.ui.components.MenuNavigation
import com.practica.buscov2.ui.components.Space
import com.practica.buscov2.ui.components.Title
import com.practica.buscov2.ui.theme.GrayPlaceholder
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileView(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    vmToken: TokenViewModel,
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
        }
    }


    if (user != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProfileV(
                Modifier.align(Alignment.Center),
                vmUser,
                vmGoogle,
                user!!,
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
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showPhotoFullScreen by remember { mutableStateOf(false) }

    LateralMenu(
        drawerState = drawerState,
        drawerContent = { list -> MenuNavigation(vmUser, vmGoogle, user, navController, list) }
    ) { scope ->
        //Mostrar foto en pantalla completa
        if (showPhotoFullScreen) {
            PhotoFullScreen(user) { showPhotoFullScreen = it }
        }

        Scaffold(modifier = modifier
            .fillMaxSize(),
            bottomBar = {
                BottomNav(navController, RoutesBottom.allRoutes)
            },
            topBar = {
                TopBarProfile(navController)
            }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 15.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //Imagen de perfil
                InsertCircleProfileImage(
                    image = user.image ?: "",
                    modifier = Modifier
                        .size(160.dp)
                        .shadow(10.dp, shape = CircleShape),
                    onClick = { showPhotoFullScreen = true }
                )

                Space(size = 4.dp)

                BoxUsername(user)

                Space(size = 4.dp)

                Title(text = "${user.name} ${user.lastname}")

                TabsPages(user)
            }
        }
    }
}

@Composable
fun TabsPages(user: User) {
    val tabs = if (user.worker == null) ItemTabProfile.pagesUser else ItemTabProfile.pagesWorker

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    Column {
        Tabs(tabs, pagerState)
        TabsContent(tabs, pagerState, user)
    }
}

@Composable
fun TabsContent(tabs: List<ItemTabProfile>, pagerState: PagerState, user: User) {
    HorizontalPager(
        state = pagerState
    ) { page ->
        tabs[page].screen(user)
    }
}

@Composable
fun Tabs(tabs: List<ItemTabProfile>, pagerState: PagerState) {
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
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
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
fun WorksCompletedProfile(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp)
    ) {

    }
}

@Composable
fun Qualifications(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp)
    ) {

    }
}

@Composable
fun Information(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp)
    ) {
        //Datos ubicacion
        ElementRowInformation(
            imageId = R.drawable.argentina,
            text = "${if (user.city.isNullOrEmpty()) user.department else user.city}, ${user.province}, ${user.country}."
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
                text = "${user.worker.workersProfessions?.first()?.profession?.name}, ${user.worker.title}",
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
                contentDescription = "Bandera",
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
fun Proposals(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow)
    ) {

        Icon(
            painter = painterResource(id = R.drawable.hand),
            contentDescription = "Propuestas",
            modifier = Modifier.size(30.dp)
        )
        Text(text = "No existen propuestas, crea una nueva.", color = GrayText)
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
            else user.worker.workersProfessions?.firstOrNull()?.profession?.name
                ?: ""

        Text(text = username, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp))
    }
}

@Composable
fun PhotoFullScreen(user: User, setShowPhotoFullScreen: (Boolean) -> Unit) {
    //ESTA COLUMNA
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
fun TopBarProfile(navController: NavHostController) {
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = { Text(text = "") },
        navigationIcon = {
            ButtonBack(
                navController = navController,
                size = 48.dp,
                modifier = Modifier.padding(start = 10.dp)
            ){
                navController.navigate("Home")
            }
        },
        actions = {
            ButtonWithIcon(
                iconId = R.drawable.edit,
                text = "Editar",
                modifier = Modifier.padding(horizontal = 15.dp)
            ){
                navController.navigate("EditProfile")
            }
        }
    )
}
