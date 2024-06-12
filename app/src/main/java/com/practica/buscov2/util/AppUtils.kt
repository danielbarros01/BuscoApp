package com.practica.buscov2.util

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class AppUtils {
    companion object {
        fun stringToLocalDateTime(dateString: String): LocalDateTime? {
            try {
                //Extrer el string hasta que empiece el punto
                val dateWithoutMillis = dateString.substringBeforeLast(".")

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                return LocalDateTime.parse(dateWithoutMillis, formatter)
            }catch (e: Exception) {
                Log.e("Error", e.message.toString())
                return null
            }
        }

        /*fun compareDates(date1: String, date2: String): Int {
            val dateOne = stringToLocalDateTime(date1)
            val dateTwo = stringToLocalDateTime(date2)

            return dateOne.compareTo(dateTwo)
        }*/

        fun expiredDate(dateString: String): Boolean {
            val date = stringToLocalDateTime(dateString)
            val dateNow = LocalDateTime.now()

            return date?.isBefore(dateNow) ?: false
        }

        fun convertToIsoDate(dateString: String, currentFormat: String): String {
            // Define el formato de entrada usando el formato proporcionado
            val inputFormat = SimpleDateFormat(currentFormat, Locale.getDefault())

            // Define el formato de salida como yyyy-MM-dd
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Convierte la cadena de fecha del formato de entrada a un objeto Date
            val date: Date = inputFormat.parse(dateString)

            // Convierte el objeto Date al formato de salida y devuelve la cadena resultante
            return outputFormat.format(date)
        }

    }

}