package com.ahmed.weather.iti.ui.notification

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ahmed.weather.iti.network.RetrofitObj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherNotificationWorker (context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun doWork(): Result {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitObj.service.getCurrentData(24.123, 12.23, "standard", "en")
            }
            val notificationHelper = WeatherNotificationHelper(applicationContext)
            notificationHelper.createNotificationChannel()
            notificationHelper.sendAlarm("${response.main?.temp}   ${response.weather?.get(0)?.description}",Uri.EMPTY)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}