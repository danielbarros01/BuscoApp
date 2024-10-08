package com.practica.buscov2.ui.views.images

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectImageFromGallery(onGalleryClosed: () -> Unit, onImageSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val permission = Manifest.permission.READ_EXTERNAL_STORAGE
    val permissionState = rememberPermissionState(permission)

    // Launcher to open the image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { imageUri ->
            if (imageUri != null) {
                val file = File.createTempFile("buscoimg", ".jpg")

                try {
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                }catch (e:Exception){

                }

                // Obtiene la Uri del archivo temporal
                val tempImageUri = Uri.fromFile(file)

                onImageSelected(tempImageUri)
            } else {
                // The gallery was closed without selecting an image
                onGalleryClosed()
            }
        }
    )

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}


