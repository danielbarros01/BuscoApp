package com.practica.buscov2.ui.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.SimpleUbication
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.navigation.RoutesDrawer
import com.practica.buscov2.ui.components.BottomNav
import com.practica.buscov2.ui.components.ButtonMenu
import com.practica.buscov2.ui.components.ButtonPrincipal
import com.practica.buscov2.ui.components.InsertCircleProfileImage
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
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    homeVm: HomeViewModel,
    vmUser: UserViewModel,
    vmToken: TokenViewModel,
    vmGoogle: GoogleLoginViewModel,
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
            Home(Modifier.align(Alignment.Center), homeVm, vmUser, vmGoogle, navController, user!!)
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
    navController: NavHostController,
    user: User
) {
    //Rutas de navegacion bottom
    val navigationRoutes = listOf(
        RoutesBottom.Home,
        RoutesBottom.New,
        RoutesBottom.Chat
    )
    val navigationRoutesDrawer = listOf(
        RoutesDrawer.Works,
        RoutesDrawer.Applications,
        RoutesDrawer.Proposals,
        RoutesDrawer.Notifications,
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var isSearchWork by remember {
        mutableStateOf(false)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuNavigation(
                vmUser = vmUser,
                vmGoogle = vmGoogle,
                user = user,
                navController = navController,
                routes = navigationRoutesDrawer
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
                        ){
                            navController.navigate("Profile")
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
                        if(user.worker != null){
                            isSearchWork = true
                        }else{
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
                            country = user.country,
                            province = user.province,
                            department = user.department,
                            city = user.city
                        )
                    )

                    if (isSearchWork) {
                        ButtonPrincipal(text = "Buscar", enabled = true) {

                        }
                    } else {
                        SearchField(context = LocalContext.current)
                    }
                }
            }
        }

    }
}

@Composable
fun ButtonUbication(ubication: SimpleUbication) {
    Button(
        onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,

            ),
        modifier = Modifier.offset(x = -(15.dp))
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "",
                tint = GrayField,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = (ubication.city ?: ubication.department) + ", " + ubication.country,
                color = GrayField,
                fontSize = 15.sp
            )
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