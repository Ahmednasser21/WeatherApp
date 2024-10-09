package com.ahmed.weather.iti.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlarm(alarmDTO: AlarmDTO)
    @Delete
    suspend fun deleteAlarm(alarmDTO: AlarmDTO)
    @Query("Select * from Alarms")
    fun getAllAlarms(): Flow<List<AlarmDTO>>
}