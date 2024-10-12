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

class WeatherNotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {
    private lateinit var unit: String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun doWork(): Result {
        return try {
            val locationPrefs =
                applicationContext.getSharedPreferences("locationData", Context.MODE_PRIVATE)
            val longitude = locationPrefs.getFloat("longitude", 0.0f).toDouble()
            val latitude = locationPrefs.getFloat("latitude", 0.0f).toDouble()
            val cityName = locationPrefs.getString("address_line", "Cairo").toString()

            val unitPrefs =
                applicationContext.getSharedPreferences("Temperature", Context.MODE_PRIVATE)
            val unitsApi = if (unitPrefs.getString("Temperature", "celsius") == "celsius") {
                unit = "°C"
                "metric"

            } else if (unitPrefs.getString("Temperature", "celsius") == "kalvin") {
                unit = "°K"
                "standard"
            } else {
                unit = "°F"
                "imperial"
            }
            val languagePrefs =
                applicationContext.getSharedPreferences("Language", Context.MODE_PRIVATE)
            val lang = if(languagePrefs.getBoolean("arabic",false)){
                "ar"
            }else{
                "en"
            }
            val response = withContext(Dispatchers.IO) {
                RetrofitObj.service.getCurrentData(latitude, longitude, unitsApi, lang)
            }
            val notificationHelper = WeatherNotificationHelper(applicationContext)
            notificationHelper.createNotificationChannel()
            notificationHelper.sendAlarm(
                "${cityName.substringBefore("G")}     ${response.main?.temp}$unit   ${response.weather?.get(0)?.description}",
                "Current Weather", Uri.EMPTY
            )
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}