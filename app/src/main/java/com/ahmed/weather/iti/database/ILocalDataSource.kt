package com.ahmed.weather.iti.database

import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    suspend fun addAlarm(alarmDTO: AlarmDTO)

    suspend fun deleteAlarm(alarmDTO: AlarmDTO)
    fun getAllAlarms(): Flow<List<AlarmDTO>>

    suspend fun addFav(favouriteDTO: FavouriteDTO)

    suspend fun deleteFav(favouriteDTO: FavouriteDTO)
    fun getAllFav(): Flow<List<FavouriteDTO>>
}