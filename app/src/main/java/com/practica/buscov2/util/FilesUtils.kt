package com.practica.buscov2.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

class FilesUtils {
    companion object {
        fun getFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
            val file = File(uri.path ?: "")
            val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(partName, file.name, requestFile)
        }

        fun sizeFile(file: File): Double {
            //Verificar que la imagen pesa menos de
            val fileSizeInBytes = file.length()
            return fileSizeInBytes / (1024.0 * 1024.0)
        }

        fun compressImage(
            context: Context,
            file: File,
            fileSizeInMb: Double,
            maxImageSize: Int
        ): File {
            // Calcular el ratio de compresión necesario
            val compressionRatio = maxImageSize / fileSizeInMb
            //Convertir el ratio a un valor de calidad
            val quality = ceil(compressionRatio * 100).toInt()
            val adjustedQuality = if (quality > 100) 100 else quality

            val bitmap = BitmapFactory.decodeFile(file.path)
            val compressedFile = File.createTempFile("compressed_img", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(compressedFile)

            //Aplicar compresion con la calidad calculada
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                adjustedQuality,
                outputStream
            )
            outputStream.flush()
            outputStream.close()

            return compressedFile
        }
    }
}
