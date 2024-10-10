package com.ahmed.weather.iti.database

import android.content.Context
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val database: DataBase) : ILocalDataSource {

    override suspend fun addAlarm(alarmDTO: AlarmDTO) {
        database.alarmDao.addAlarm(alarmDTO)
    }

    override suspend fun deleteAlarm(alarmDTO: AlarmDTO) {
        database.alarmDao.deleteAlarm(alarmDTO)
    }

    override fun getAllAlarms(): Flow<List<AlarmDTO>> {
        return database.alarmDao.getAllAlarms()
    }

    override suspend fun addFav(favouriteDTO: FavouriteDTO) {
        database.favouriteDAO.addFav(favouriteDTO)
    }

    override suspend fun deleteFav(favouriteDTO: FavouriteDTO) {
        database.favouriteDAO.deleteFav(favouriteDTO)
    }

    override fun getAllFav(): Flow<List<FavouriteDTO>> {
        return database.favouriteDAO.getAllFav()
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(context: Context): LocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val database = DataBase.getInstance(context)
                val instance = LocalDataSource(database)
                INSTANCE = instance
                instance
            }
        }
    }
}