package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.AlarmDTO
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.database.DataBase
import com.ahmed.weather.iti.network.RetrofitObj
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository private constructor(private val retrofit:RetrofitObj,private val dataBase: DataBase) {

    companion object{
        private var INSTANCE: Repository? = null
        fun getInstance(retrofit:RetrofitObj, dataBase: DataBase):Repository{
            return INSTANCE?: synchronized(this){
                val instance = Repository(retrofit,dataBase)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun getWeatherForecast(longitude:Double,latitude:Double,units:String,lang:String): Flow<WeatherForecastResponse>{
        return flow {
                val hourlyForecast = retrofit.service.getForecastData(longitude,latitude, units, lang)
                emit(hourlyForecast)
                delay(100)
        }
    }
    suspend fun getCurrentWeather(longitude:Double,latitude:Double,units:String,lang:String): Flow<WeatherCurrentResponse>{
        return flow {
                val currentWeather = retrofit.service.getCurrentData(longitude,latitude, units, lang)
                emit(currentWeather)
                delay(100)

        }
    }
    suspend fun addFav(favouriteDTO: FavouriteDTO){
        dataBase.favouriteDAO.addFav(favouriteDTO)
    }
    suspend fun removeFav(favouriteDTO: FavouriteDTO){
        dataBase.favouriteDAO.deleteFav(favouriteDTO)
    }
    fun getAllFav():Flow<List<FavouriteDTO>>{
        return dataBase.favouriteDAO.getAllFav()
    }
    suspend fun addAlarm(alarmDTO: AlarmDTO){
        dataBase.alarmDao.addAlarm(alarmDTO)
    }
    suspend fun removeAlarm(alarmDTO: AlarmDTO){
        dataBase.alarmDao.deleteAlarm(alarmDTO)
    }
    fun getAllAlarms():Flow<List<AlarmDTO>>{
        return dataBase.alarmDao.getAllAlarms()
    }

}