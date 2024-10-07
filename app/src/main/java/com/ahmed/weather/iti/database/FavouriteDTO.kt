package com.ahmed.weather.iti.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavouriteLocations")
data class FavouriteDTO (@PrimaryKey val cityName:String, val longitude:Double, val latitude:Double)