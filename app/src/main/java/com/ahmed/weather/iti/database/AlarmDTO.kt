package com.ahmed.weather.iti.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarms")
data class AlarmDTO (val formattedTime:String, @PrimaryKey val timeInMillis:Long)