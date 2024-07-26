package com.practica.buscov2.util

import java.time.LocalDate

class Constants {
    companion object{
        const val API_URL = "192.168.100.7"
        const val ESQ_HOST = "http://localhost:5029"
        const val BASE_URL = "http://$API_URL:5029/api/"
        const val ENDPOINT_USERS = "users"
        const val ENDPOINT_LOGIN = "login"
        const val ENDPOINT_REGISTER = "create"
        const val ENDPOINT_MY_PROFILE = "me"
        const val ENDPOINT_PHOTO = "image"
        const val CONFIRM_REGISTER = "confirm-register-code"
        const val CONFIRM_PASSWORD_CODE = "confirm-password-code"
        const val RESEND_CODE = "resend-code"
        const val SEND_CODE = "send-code"
        const val CHANGE_PASSWORD = "change-password"
        const val GOOGLE_LOGIN = "google/signin"

        const val ENDPOINT_PROFESSIONS = "professions"
        const val ENDPOINT_CATEGORIES = "categories"

        const val ENDPOINT_WORKERS = "workers"

        const val ENDPOINT_PROPOSALS = "proposals"
        const val ENDPOINT_APPLICATIONS = "applications"
        const val ENDPOINT_JOBS = "jobs"
        const val ENDPOINT_QUALIFICATIONS = "qualifications"

        const val ENDPOINT_PRUEBA = "weatherforecast"
    }
}