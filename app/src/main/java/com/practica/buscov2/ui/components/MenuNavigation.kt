package com.practica.buscov2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.gson.Gson
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.User
import com.practica.buscov2.navigation.RoutesBottom
import com.practica.buscov2.navigation.RoutesDrawer
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayText
import com.practica.buscov2.ui.theme.OrangePrincipal
import com.practica.buscov2.ui.viewModel.HomeViewModel
import com.practica.buscov2.ui.viewModel.auth.GoogleLoginViewModel
import com.practica.buscov2.ui.viewModel.auth.TokenViewModel
import com.practica.buscov2.ui.viewModel.users.UserViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun LateralMenu(
    drawerState: DrawerState,
    drawerContent: @Composable (List<RoutesDrawer>) -> Unit,
    content: @Composable (CoroutineScope) -> Unit
) {
    val scope = rememberCoroutineScope()
    val menuItems = RoutesDrawer.allRoutes
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { drawerContent(menuItems) }) {
        content(scope)
    }
}

@Composable
fun MenuNavigation(
    vmUser: UserViewModel,
    vmGoogle: GoogleLoginViewModel,
    user: User,
    navController: NavHostController,
    routes: List<RoutesDrawer>
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                InsertImage(R.drawable.logo, modifier = Modifier.size(100.dp))
                Space(size = 10.dp)

                routes.forEach { item ->
                    ItemNavigation(
                        item.icon,
                        "Ir a ${item.title}",
                        item.title
                    ) {
                        navController.navigate(item.route)
                    }

                    HorizontalDivider()
                }
            }

            Column {
                Column {
                    HorizontalDivider()
                    ItemNavigation(
                        R.drawable.settings,
                        "Ir a Configuración",
                        "Configuración"
                    ) {
                        navController.navigate("Configuration")
                    }

                    HorizontalDivider()
                    ItemNavigation(
                        R.drawable.exit,
                        "Cerrar sesión",
                        "Cerrar Sesión"
                    ) {
                        vmUser.logout {
                            vmGoogle.signOut()

                            navController.navigate("Login") {
                                popUpTo("Home") {
                                    inclusive = true
                                } // asegura que se elimine la pantalla "Home"
                            }
                        }
                    }
                }
                HorizontalDivider(thickness = 3.dp)
                ItemProfileNavigation(user) {
                    navController.navigate("Profile/${user.id}")
                }
            }

        }

    }
}

@Composable
fun ItemProfileNavigation(user: User, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clickable { onClick() }
    ) {
        InsertCircleProfileImage(
            image = user.image ?: "",
            modifier = Modifier.size(50.dp)
        )

        Space(size = 8.dp)

        Column {
            Text(
                text = "${user.name} ${user.lastname}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${user.email}",
                fontSize = 12.sp,
                color = GrayField
            )
        }
    }
}

@Composable
fun ItemNavigation(iconId: Int, iconDescription: String, text: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = iconDescription,
                modifier = Modifier.size(30.dp),
                tint = GrayText
            )
        },
        label = {
            Text(text = text, fontSize = 20.sp)
        },
        selected = false,
        onClick = { onClick() }
    )
}


@Composable
fun BottomNav(navHostController: NavHostController, routes: List<RoutesBottom>) {
    BottomAppBar(containerColor = Color.White, modifier = Modifier.height(62.dp)) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 35.dp)
        ) {
            val currentRoute = CurrentRoute(navHostController)

            routes.forEach { item ->
                NavigationBarItem(
                    false,
                    onClick = { navHostController.navigate(item.route) },
                    icon = {
                        Column {
                            Icon(
                                painter = if (currentRoute == item.route) painterResource(id = item.iconSelected) else
                                    painterResource(id = item.icon),
                                contentDescription = item.title,
                                tint = OrangePrincipal,
                                modifier = Modifier.size(34.dp)
                            )

                            Space(size = 1.dp)

                            if (currentRoute == item.route) {
                                Spacer(
                                    modifier = Modifier
                                        .height(2.dp)
                                        .width(34.dp)
                                        .background(OrangePrincipal)
                                )
                            }
                        }


                    }, modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

@Composable
fun CurrentRoute(navHostController: NavHostController): String? {
    val current by navHostController.currentBackStackEntryAsState()
    return current?.destination?.route //ruta actual
}







