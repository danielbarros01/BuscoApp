package com.practica.buscov2.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FilesUtils {
    companion object{
        fun getFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
            val file = File(uri.path ?: "")
            val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }
    }
}