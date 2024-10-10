package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.database.LocalDataSource
import com.ahmed.weather.iti.network.RemoteDataSource
import com.ahmed.weather.iti.network.RetrofitObj
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) :
    IRepository {

    companion object {
        private var INSTANCE: Repository? = null
         fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteDataSource, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getWeatherForecast(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherForecastResponse> {
        return flow {
            val hourlyForecast = remoteDataSource.getForecastData(longitude, latitude, units, lang)
            emit(hourlyForecast)
            delay(100)
        }
    }

    override suspend fun getCurrentWeather(
        longitude: Double,
        latitude: Double,
        units: String,
        lang: String
    ): Flow<WeatherCurrentResponse> {
        return flow {
            val currentWeather = remoteDataSource.getCurrentData(longitude, latitude, units, lang)
            emit(currentWeather)
            delay(100)

        }
    }

    override suspend fun addFav(favouriteDTO: FavouriteDTO) {
        localDataSource.addFav(favouriteDTO)
    }

    override suspend fun removeFav(favouriteDTO: FavouriteDTO) {
        localDataSource.deleteFav(favouriteDTO)
    }

    override fun getAllFav(): Flow<List<FavouriteDTO>> {
        return localDataSource.getAllFav()
    }

    override suspend fun addAlarm(alarmDTO: AlarmDTO) {
        localDataSource.addAlarm(alarmDTO)
    }

    override suspend fun removeAlarm(alarmDTO: AlarmDTO) {
        localDataSource.deleteAlarm(alarmDTO)
    }

    override fun getAllAlarms(): Flow<List<AlarmDTO>> {
        return localDataSource.getAllAlarms()
    }

}