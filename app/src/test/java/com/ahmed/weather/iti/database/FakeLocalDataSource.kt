package com.ahmed.weather.iti.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource : ILocalDataSource {
    private val alarms = mutableListOf<AlarmDTO>()
    private val favourites = mutableListOf<FavouriteDTO>()

    override suspend fun addAlarm(alarmDTO: AlarmDTO) {
        alarms.add(alarmDTO)
    }

    override suspend fun deleteAlarm(alarmDTO: AlarmDTO) {
        alarms.remove(alarmDTO)
    }

    override fun getAllAlarms(): Flow<List<AlarmDTO>> {
        return flow { emit(alarms.toList()) }
    }

    override suspend fun addFav(favouriteDTO: FavouriteDTO) {
        favourites.add(favouriteDTO)
    }

    override suspend fun deleteFav(favouriteDTO: FavouriteDTO) {
        favourites.remove(favouriteDTO)
    }

    override fun getAllFav(): Flow<List<FavouriteDTO>> {
        return flow { emit(favourites.toList()) }
    }
}