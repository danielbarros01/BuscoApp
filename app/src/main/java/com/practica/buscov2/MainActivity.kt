package com.practica.buscov2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.practica.buscov2.navigation.NavManager
import com.practica.buscov2.ui.theme.BuscoV2Theme
import com.practica.buscov2.ui.viewModel.CheckEmailViewModel
import com.practica.buscov2.ui.viewModel.CompleteDataViewModel
import com.practica.buscov2.ui.viewModel.GeorefViewModel
import com.practica.buscov2.ui.viewModel.LoginViewModel
import com.practica.buscov2.ui.viewModel.RegisterViewModel
import com.practica.buscov2.ui.views.LoginView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel : LoginViewModel by viewModels()
        val completeDataViewModel : CompleteDataViewModel by viewModels()
        val georefViewModel : GeorefViewModel by viewModels()
        val checkEmailViewModel : CheckEmailViewModel by viewModels()
        val registerViewModel : RegisterViewModel by viewModels()

        setContent {
            BuscoV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager()
                }
            }
        }
    }
}
