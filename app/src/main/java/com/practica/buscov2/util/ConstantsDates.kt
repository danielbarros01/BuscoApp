package com.practica.buscov2.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class ConstantsDates {
    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        val MIN_DATE_BIRTHDATE: LocalDate = LocalDate.of(1950, 1, 1)
        @RequiresApi(Build.VERSION_CODES.O)
        val MAX_DATE_BIRTHDATE: LocalDate = LocalDate.now().minusYears(18)
    }
}