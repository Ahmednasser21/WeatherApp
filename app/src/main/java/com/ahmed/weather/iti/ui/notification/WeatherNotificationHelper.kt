package com.ahmed.weather.iti.ui.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ahmed.weather.iti.R

class WeatherNotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "notification_channel_id"
        private const val ALARM_NOTIFICATION_ID = 2002
        private var selectedSoundUri: Uri = Settings.System.DEFAULT_ALARM_ALERT_URI
        private var ringtone: Ringtone? = null
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather Notifications"
                setSound(null, null)
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun sendAlarm(contentText: String, title: String, uri: Uri) {
        val dismissIntent = Intent(context, WeatherNotificationReceiver::class.java).apply {
            action = "DISMISS_ACTION"
            putExtra("NOTIFICATION_ID", ALARM_NOTIFICATION_ID)
            putExtra("EXTRA_SOUND_URI", uri.toString())
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .addAction(
                0,
                "Dismiss",
                dismissPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build())

        playAlarmSound(context, uri)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, notificationTime: Long, soundUri: Uri?) {
        selectedSoundUri = soundUri ?: Settings.System.DEFAULT_ALARM_ALERT_URI
        val intent = Intent(context, WeatherNotificationReceiver::class.java).apply {
            putExtra("EXTRA_SOUND_URI", soundUri.toString())
        }
        val requestCode = notificationTime.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
    }

    fun cancelScheduledAlarm(alarmId: Int) {
        val intent = Intent(context, WeatherNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        stopAlarmSound()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(alarmId)
    }

    private fun playAlarmSound(context: Context, soundUri: Uri?) {
        ringtone?.stop()
        ringtone = RingtoneManager.getRingtone(context, soundUri ?: selectedSoundUri)
        ringtone?.play()
    }

    fun stopAlarmSound() {
        ringtone?.stop()
        ringtone = null
    }
}