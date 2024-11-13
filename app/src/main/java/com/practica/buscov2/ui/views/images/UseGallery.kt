package com.practica.buscov2.ui.views.images

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.practica.buscov2.util.Config.Companion.MAX_IMAGE_SIZE
import com.practica.buscov2.util.FilesUtils.Companion.sizeFile
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SelectImageFromGallery(
    onGalleryClosed: () -> Unit,
    onError: (String) -> Unit = {},
    onImageSelected: (Uri) -> Unit
) {
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

                    if (sizeFile(file) <= MAX_IMAGE_SIZE) {
                        // Obtiene la Uri del archivo temporal
                        val tempImageUri = Uri.fromFile(file)
                        onImageSelected(tempImageUri)
                    } else {
                        onError("La imagen no puede pesar más de ${MAX_IMAGE_SIZE}mb")
                        onGalleryClosed()
                    }
                } catch (_: Exception) {
                    onError("Error al procesar la imagen, intentalo de nuevo")
                    onGalleryClosed()
                }
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


