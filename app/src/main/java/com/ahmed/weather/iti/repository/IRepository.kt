package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.database.LocalDataSource
import com.ahmed.weather.iti.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getWeatherForecast(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse>

    suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherCurrentResponse>

    suspend fun addFav(favouriteDTO: FavouriteDTO)
    suspend fun removeFav(favouriteDTO: FavouriteDTO)
    fun getAllFav(): Flow<List<FavouriteDTO>>
    suspend fun addAlarm(alarmDTO: AlarmDTO)
    suspend fun removeAlarm(alarmDTO: AlarmDTO)
    fun getAllAlarms(): Flow<List<AlarmDTO>>

}