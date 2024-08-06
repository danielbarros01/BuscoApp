package com.practica.buscov2.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.practica.buscov2.R
import kotlin.random.Random

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun showBasicNotification(){
        val notification = NotificationCompat.Builder(context,"123")
            .setContentTitle("Titulo")
            .setContentText("Lorem ipsum dolor sit amet.")
            .setSmallIcon(R.drawable.notification)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(
            Random.nextInt(),
            notification
        )
    }
}