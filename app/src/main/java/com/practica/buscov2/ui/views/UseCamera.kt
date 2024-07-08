package com.practica.buscov2.ui.views

import android.Manifest
import android.net.Uri
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.practica.buscov2.R
import com.practica.buscov2.ui.components.ArrowBack
import com.practica.buscov2.ui.components.ArrowSquareBack
import com.practica.buscov2.ui.components.ButtonBack
import com.practica.buscov2.ui.components.Circle
import com.practica.buscov2.ui.theme.OrangePrincipal
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UseCamera(onBack: () -> Unit, onImageCaptured: (Uri) -> Unit) {
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    // Estado para la cámara seleccionada (trasera o delantera)
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(99f),
        containerColor = Color.Black,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    //Al sacar foto
                    val executor = ContextCompat.getMainExecutor(context)
                    takePicture(cameraController, executor, onImageCaptured)
                },
                shape = CircleShape, modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Circle(color = Color.White, size = 60.dp, modifier = Modifier.padding(5.dp))
            }
        },
        topBar = {
            TopAppBar(
                title = { /*TODO*/ },
                navigationIcon = {
                    IconButton(onClick = { onBack() }, modifier = Modifier.padding(15.dp)) {
                        ArrowBack()
                    }
                },
                actions = {
                    IconButton(onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                        cameraController.cameraSelector = cameraSelector
                    }, modifier = Modifier.padding(15.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.changecamera),
                            contentDescription = "",
                            tint = OrangePrincipal,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            if (permissionState.status.isGranted) {
                Camera(
                    modifier = Modifier,
                    cameraController = cameraController,
                    lifecycle = lifecycle
                )
            } else {
                Text(text = "Denegado", modifier = Modifier.padding(it))
            }
        }
    }

}

private fun takePicture(cameraController: LifecycleCameraController, executor: Executor, onImageCaptured: (Uri) -> Unit) {
    val file = File.createTempFile("buscoimg", ".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()

    cameraController.takePicture(
        outputDirectory,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { onImageCaptured(it) }
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error, no se saco la foto")
            }
        })

}

@Composable
fun Camera(
    modifier: Modifier = Modifier,
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner
) {
    cameraController.bindToLifecycle(lifecycle)
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, //width
                    ViewGroup.LayoutParams.MATCH_PARENT //weight
                )
            }

            previewView.controller = cameraController

            previewView
        }, modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(bottom = 20.dp)
    )
}