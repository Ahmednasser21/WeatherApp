package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.database.FakeLocalDataSource
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.network.FakeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository (private val fakeRemoteDataSource: FakeRemoteDataSource,
                      private val fakeLocalDataSource: FakeLocalDataSource
): IRepository {
    var forecastResponse: WeatherForecastResponse? = null
    var currentWeatherResponse: WeatherCurrentResponse? = null
    var favouriteList: MutableList<FavouriteDTO> = mutableListOf()
    var alarmList: MutableList<AlarmDTO> = mutableListOf()

    var shouldThrowError: Boolean = false

    override suspend fun getWeatherForecast(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        return flow {
            if (shouldThrowError) {
                throw Exception("Fake error fetching weather forecast")
            }
            forecastResponse?.let { emit(it) }
                ?: throw Exception("No forecast response set")
        }
    }

    override suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherCurrentResponse> {
        return flow {
            if (shouldThrowError) {
                throw Exception("Fake error fetching current weather")
            }
            currentWeatherResponse?.let { emit(it) }
                ?: throw Exception("No current weather response set")
        }
    }

    override suspend fun addFav(favouriteDTO: FavouriteDTO) {
        favouriteList.add(favouriteDTO)
    }

    override suspend fun removeFav(favouriteDTO: FavouriteDTO) {
        favouriteList.remove(favouriteDTO)
    }

    override fun getAllFav(): Flow<List<FavouriteDTO>> {
        return flow { emit(favouriteList) }
    }

    override suspend fun addAlarm(alarmDTO: AlarmDTO) {
        alarmList.add(alarmDTO)
    }

    override suspend fun removeAlarm(alarmDTO: AlarmDTO) {
        alarmList.remove(alarmDTO)
    }

    override fun getAllAlarms(): Flow<List<AlarmDTO>> {
        return flow { emit(alarmList) }
    }
}