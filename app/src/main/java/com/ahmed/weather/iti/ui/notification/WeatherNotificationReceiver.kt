package com.ahmed.weather.iti.ui.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

private const val TAG = "WeatherNotificationRece"
class WeatherNotificationReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = WeatherNotificationHelper(context)

        val action = intent.action
        if (action == "DISMISS_ACTION") {
            val notificationId = intent.getIntExtra("NOTIFICATION_ID", 2002)
            notificationHelper.stopAlarmSound()

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(notificationId)
        } else {
            val soundUri = Uri.parse(intent.getStringExtra("EXTRA_SOUND_URI"))
            notificationHelper.createNotificationChannel()
            notificationHelper.sendAlarm("Check the weather.", soundUri)
        }
    }
}