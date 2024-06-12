package com.practica.buscov2.util

import java.time.LocalDate

class ConstantsDates {
    companion object{
        val MIN_DATE_BIRTHDATE = LocalDate.of(1950, 1, 1)
        val MAX_DATE_BIRTHDATE = LocalDate.now().minusYears(18)
    }
}