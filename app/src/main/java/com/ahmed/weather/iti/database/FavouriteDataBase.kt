package com.ahmed.weather.iti.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FavouriteDTO::class], version = 1)
abstract class FavouriteDataBase : RoomDatabase() {
    abstract val favouriteDAO: FavouriteDAO

    companion object {
        private var INSTANCE:FavouriteDataBase? = null

        fun getInstance(context: Context):FavouriteDataBase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavouriteDataBase::class.java,
                    "FavouriteLocations"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}