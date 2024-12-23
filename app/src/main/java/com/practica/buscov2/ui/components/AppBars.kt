package com.practica.buscov2.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.practica.buscov2.ui.theme.OrangePrincipal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        },
        navigationIcon = {
            ButtonMenu(modifier = Modifier.padding(horizontal = 5.dp)) {
                scope.launch {
                    drawerState.apply {
                        if (isClosed) open() else close()
                    }
                }
            }
        },
        colors = TopAppBarColors(
            containerColor = OrangePrincipal,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.Black,
            scrolledContainerColor = Color.Red,
            actionIconContentColor = Color.Cyan
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithBack(
    modifier: Modifier = Modifier,
    title: String,
    navController: NavController
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            ButtonBack(modifier = Modifier.padding(10.dp), size = 44.dp, navController = navController)
        },
        colors = TopAppBarColors(
            containerColor = OrangePrincipal,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.Black,
            scrolledContainerColor = Color.Red,
            actionIconContentColor = Color.Cyan
        ),
    )
}