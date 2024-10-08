package com.ahmed.weather.iti.ui.notification

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ahmed.weather.iti.R

class WeatherNotificationHelper(private val context: Context) {

    companion object{
        private const val CHANNEL_ID = "notification_channel_id"
        private const val PERMISSION_REQUEST_CODE = 1001
    }


    fun createNotificationChannel() {

        val channel = NotificationChannel(CHANNEL_ID, "Weather Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "This is a weather notification channel"
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun sendNotification(contentText: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.cloudy)
            .setContentTitle("Weather")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(null)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    (context as Activity),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
                return
            }
            notify(1, builder.build())
        }
    }
}