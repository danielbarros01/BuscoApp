package com.practica.buscov2.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class AppUtils {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private fun stringToLocalDateTime(dateString: String): LocalDateTime? {
            try {
                //Extrer el string hasta que empiece el punto
                val dateWithoutMillis = dateString.substringBeforeLast(".")

                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                return LocalDateTime.parse(dateWithoutMillis, formatter)
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                return null
            }
        }

        /*fun compareDates(date1: String, date2: String): Int {
            val dateOne = stringToLocalDateTime(date1)
            val dateTwo = stringToLocalDateTime(date2)

            return dateOne.compareTo(dateTwo)
        }*/

        @RequiresApi(Build.VERSION_CODES.O)
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
            val date: Date? = inputFormat.parse(dateString)

            // Convierte el objeto Date al formato de salida y devuelve la cadena resultante
            return outputFormat.format(date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun String.toLocalDate(): LocalDate {
            return LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

        /**
         * Formatea un número representado como String agregando puntos como separadores de miles.
         *
         * @param value El número en formato de cadena.
         * @return El número formateado con separadores de miles, o el valor original si ocurre un error.
         */
        fun formatNumber(value: String): String {
            return try {
                // Elimina los puntos existentes y convierte el valor a Long.
                val number = value.replace(".", "").toLong()
                //HAY UN PUNTO AL FINAL QUE NO VA

                // Crea un formato decimal que usa puntos como separadores de miles.
                val decimalFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.GERMAN))
                // Formatea el número y lo devuelve como String.
                decimalFormat.format(number)
            } catch (e: NumberFormatException) {
                // Si ocurre un error (por ejemplo, si el valor no es un número válido), devuelve el valor original.
                value
            }
        }


        /**
         * Compara dos valores numéricos representados como String para verificar si el segundo es mayor que el primero.
         *
         * @param from El valor inicial en formato de cadena.
         * @param to El valor final en formato de cadena.
         * @return true si el valor final es mayor que el valor inicial, false en caso contrario.
         */
        fun isGreaterThan(from: String, to: String): Boolean {
            return try {
                // Elimina los puntos y convierte los valores a Long, utilizando 0 si la conversión falla.
                val fromValue = from.replace(".", "").toLongOrNull() ?: 0L
                val toValue = to.replace(".", "").toLongOrNull() ?: 0L
                // Compara los valores y devuelve true si el valor final es mayor que el inicial.
                toValue > fromValue
            } catch (e: NumberFormatException) {
                // Si ocurre un error (por ejemplo, si los valores no son números válidos), devuelve false.
                false
            }
        }

        /**
         * Transforma fecha en mes MMM y dia D
         * */


        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateCard(inputDate: String): String {
            //Parseo de string a fecha
            val localDate = LocalDate.parse(inputDate.substring(0, 10))
            //Defino el formato de salida
            val formatter = DateTimeFormatter.ofPattern("MMM dd")

            return localDate.format(formatter)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun daysAgo(dateString: String): Long {
            if (dateString.isEmpty()) {
                return 0
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val givenDate = LocalDateTime.parse(dateString, formatter).toLocalDate()
            val currentDate = LocalDateTime.now().toLocalDate()
            return ChronoUnit.DAYS.between(givenDate, currentDate)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatHours(dateString: String): String {
            return try {
                val dateWithoutMillis = dateString.substringBeforeLast(".")
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val givenDateTime = LocalDateTime.parse(dateWithoutMillis, formatter)
                val givenDate = givenDateTime.toLocalDate()
                val currentDate = LocalDateTime.now().toLocalDate()

                when (val cantDays = ChronoUnit.DAYS.between(givenDate, currentDate)) {
                    0L -> {
                        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                        givenDateTime.format(timeFormatter)
                    }
                    1L -> "Ayer"
                    2L -> "Anteayer"
                    else -> "Hace $cantDays días"
                }
            } catch (e: DateTimeParseException) {
                "Fecha inválida"
            }
        }

        fun starsToPercentage(value:Int):Int{
            //MIN 0
            //MAX 5
            return value * 20;
        }
    }
}