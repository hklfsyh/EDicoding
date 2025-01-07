package com.example.edicoding.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.edicoding.R
import com.example.edicoding.api.RetrofitClient
import com.example.edicoding.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DailyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DailyReminderWorker", "doWork called")
        return try {
            val event = getNearestActiveEvent() // Mendapatkan event terdekat
            if (event == null) {
                Log.d("DailyReminderWorker", "No active event found")
            }
            event?.let {
                showNotification(it) // Menampilkan notifikasi
            }
            Result.success()
        } catch (e: HttpException) {
            Log.e("DailyReminderWorker", "Error fetching event: ${e.message}")
            Result.failure()
        }
    }

    private suspend fun getNearestActiveEvent(): Event? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getNearestActiveEvent(limit = 1)
                if (response.isSuccessful) {
                    response.body()?.listEvents?.firstOrNull()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun showNotification(event: Event) {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, "daily_reminder_channel")
            .setSmallIcon(R.drawable.ic_event)
            .setContentTitle("Event Terdekat")
            .setContentText("Event terdekat: ${event.name} - ${event.beginTime}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Gunakan NotificationManager langsung untuk menampilkan notifikasi
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminder"
            val descriptionText = "Channel untuk daily reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("daily_reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
