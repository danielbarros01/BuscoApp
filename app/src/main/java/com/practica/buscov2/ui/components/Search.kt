package com.practica.buscov2.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(context: Context) {
    val colors1 = SearchBarDefaults.colors()

    var query by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.fillMaxWidth(),
                query = query,
                onQueryChange = { it ->
                    query = it
                    active = query.isNotEmpty()
                },
                onSearch = {
                    Toast.makeText(
                        context,
                        "Search",
                        Toast.LENGTH_SHORT
                    ).show()

                    active = false
                },
                expanded = active, //active
                onExpandedChange = {}, //onactivechange
                colors = colors1.inputFieldColors,
                leadingIcon = {
                    IconButton(onClick = {
                        active = false
                        //Lo mismo que en OnSearch
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "",
                            tint = GrayField
                        )
                    }
                },
                trailingIcon = {
                    if (active) {
                        IconButton(onClick = { active = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "",
                                tint = GrayField
                            )
                        }
                    }

                },
                placeholder = {
                    Text(text = "Busca", color = GrayPlaceholder)
                }
            )
        },
        expanded = active, //active
        onExpandedChange = { }, //onactivechange
        colors = colors1,
        content = {} //donde mostramos resultados de busqueda
    )
}