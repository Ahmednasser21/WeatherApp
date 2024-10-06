package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.network.RetrofitObj
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository private constructor(private val retrofit:RetrofitObj) {

    companion object{
        private var INSTANCE: Repository? = null
        fun getInstance(retrofit:RetrofitObj):Repository{
            return INSTANCE?: synchronized(this){
                val instance = Repository(retrofit)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getWeatherForecast(longitude:Double,latitude:Double,units:String,lang:String): Flow<WeatherForecastResponse>{
        return flow {
            while(true) {
                val hourlyForecast = retrofit.service.getForecastData(longitude,latitude, units, lang)
                emit(hourlyForecast)
                delay(100)
            }
        }
    }
    suspend fun getCurrentWeather(longitude:Double,latitude:Double,units:String,lang:String): Flow<WeatherCurrentResponse>{
        return flow {
            while(true) {
                val currentWeather = retrofit.service.getCurrentData(longitude,latitude, units, lang)
                emit(currentWeather)
                delay(100)
            }
        }
    }

}