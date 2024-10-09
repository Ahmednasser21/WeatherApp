package com.ahmed.weather.iti.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FavouriteDTO::class,AlarmDTO::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract val favouriteDAO: FavouriteDAO
    abstract val alarmDao:AlarmDAO

    companion object {
        private var INSTANCE:DataBase? = null

        fun getInstance(context: Context):DataBase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "FavouriteLocations"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}