package com.ahmed.weather.iti.repository

import WeatherForecastResponse
import android.annotation.SuppressLint
import android.content.Context
import com.ahmed.weather.iti.WeatherCurrentResponse
import com.ahmed.weather.iti.database.FavouriteDTO
import com.ahmed.weather.iti.database.FavouriteDataBase
import com.ahmed.weather.iti.network.RetrofitObj
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository private constructor(private val retrofit:RetrofitObj,private val favouriteDataBase: FavouriteDataBase) {

    companion object{
        private var INSTANCE: Repository? = null
        fun getInstance(retrofit:RetrofitObj , favouriteDataBase: FavouriteDataBase):Repository{
            return INSTANCE?: synchronized(this){
                val instance = Repository(retrofit,favouriteDataBase)
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
        favouriteDataBase.favouriteDAO.addFav(favouriteDTO)
    }
    suspend fun removeFav(favouriteDTO: FavouriteDTO){
        favouriteDataBase.favouriteDAO.deleteFav(favouriteDTO)
    }
    fun getAllFav():Flow<List<FavouriteDTO>>{
        return favouriteDataBase.favouriteDAO.getAllFav()
    }

}