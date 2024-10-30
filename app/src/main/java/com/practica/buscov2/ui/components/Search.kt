package com.practica.buscov2.ui.components

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.practica.buscov2.R
import com.practica.buscov2.ui.theme.GrayField
import com.practica.buscov2.ui.theme.GrayInput
import com.practica.buscov2.ui.theme.GrayPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    color: Color = GrayInput,
    value: String = "",
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    val colors1 = SearchBarDefaults.colors(
        containerColor = color,
    )

    var query by remember {
        mutableStateOf(value)
    }
    var active by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(value) {
        query = value
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                query = query,
                onQueryChange = {
                    query = it
                    active = query.isNotEmpty()
                    onQueryChange(it)
                },
                onSearch = {
                    active = false
                    onSearch()
                },
                expanded = active, //active
                onExpandedChange = {}, //onactivechange
                colors = colors1.inputFieldColors,
                leadingIcon = {
                    IconButton(onClick = {
                        //Lo mismo que en OnSearch
                        onSearch()
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
        colors = colors1
    ) { content { active = false } } //donde mostramos resultados de busqueda

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProfession(
    color: Color = GrayField,
    text: String = "",
    lastSelectedCount: Int = 0,
    onQueryChange: (String) -> Unit = {},
    content: @Composable () -> Unit
) {
    val colors1 = SearchBarDefaults.colors(
        containerColor = Color.Transparent,
        dividerColor = Color.Transparent,
    )

    var query by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }

    // LaunchedEffect para actualizar el estado de la consulta interna cuando cambia el texto externo
    LaunchedEffect(text, lastSelectedCount) {
        if (text != query) {
            query = text
            active = false
        }
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(
                        1.dp,
                        color = if (query.isNotEmpty()) color else GrayField,
                        shape = RoundedCornerShape(12.dp)
                    ),
                query = query,
                onQueryChange = {
                    query = it
                    active = query.isNotEmpty()
                    onQueryChange(it)
                },
                onSearch = {
                    active = false
                },
                expanded = active, //active
                onExpandedChange = {}, //onactivechange
                colors = colors1.inputFieldColors,
                placeholder = {
                    Text(text = "Ej. Carpíntero", color = GrayPlaceholder)
                }
            )
        },
        expanded = active, //active
        onExpandedChange = { }, //onactivechange
        colors = colors1,
        content = {
            content()
        }
    )
}
