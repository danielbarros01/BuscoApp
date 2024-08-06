package com.practica.buscov2.notifications

import android.app.NotificationManager
import android.content.Context
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

class NotificationWorker(context: Context, params:WorkerParameters): Worker(context, params) {

    override fun doWork(): Result {
        val message = inputData.getString("MESSAGE") ?: return Result.failure()
        showNotification(message)
        return Result.success()
    }

    private fun showNotification(message: String) {
        val notification = NotificationCompat.Builder(applicationContext, "notification_one")
            .setContentTitle("Notificación de BuscoApp")
            .setContentText(message)
            .setSmallIcon(R.drawable.notification)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notification)
    }

    companion object{
        fun releaseNotification(context:Context, n: Notification){
            val constraints = Constraints.Builder()
                .build()

            val inputData = Data.Builder()
                .putString("MESSAGE", n.text)
                .build()

            val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(notificationWork)
        }
    }
}