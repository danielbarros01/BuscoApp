package com.practica.buscov2.notifications

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.practica.buscov2.R
import com.practica.buscov2.model.busco.Notification
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        try {
            val title = inputData.getString("TITLE") ?: return Result.failure()
            val message = inputData.getString("MESSAGE") ?: return Result.failure()
            val type = inputData.getString("TYPE") ?: return Result.failure()

            val icon = if (type == "MESSAGE") R.drawable.message else R.drawable.notification

            showNotification(message, title, icon)

            return Result.success()
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
            return Result.failure()
        }
    }

    private fun showNotification(
        message: String,
        title: String = "Notificación de BuscoApp",
        icon: Int
    ) {
        try {
            val notification = NotificationCompat.Builder(applicationContext, "notification_one")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Random.nextInt(), notification)
        } catch (e: Exception) {
            Log.d("Error", e.message.toString())
        }
    }

    companion object {
        fun releaseNotification(context: Context, n: Notification) {
            try {
                val constraints = Constraints.Builder()
                    .build()

                val inputData = Data.Builder()
                    .putString("TITLE", n.title)
                    .putString("MESSAGE", n.text)
                    .putString("TYPE", n.notificationType)
                    .build()

                val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .setInitialDelay(1, TimeUnit.SECONDS)
                    .build()

                WorkManager.getInstance(context).enqueue(notificationWork)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }

        }
    }
}