package com.practica.buscov2.util

import com.google.gson.Gson
import com.practica.buscov2.model.busco.auth.ErrorBusco

class ServerUtils {
    companion object{
        fun gsonError(response: retrofit2.Response<*>): ErrorBusco {
            val errorBody = response.errorBody()
            val gson = Gson()
            val errorResponse: ErrorBusco =
                gson.fromJson(errorBody?.charStream(), ErrorBusco::class.java)

            return errorResponse
        }
    }
}