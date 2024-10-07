package com.ahmed.weather.iti.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFav(favouriteDTO: FavouriteDTO)

    @Query("Select * From FavouriteLocations")
    fun getAllFav(): Flow<List<FavouriteDTO>>

    @Delete
    suspend fun deleteFav(favouriteDTO: FavouriteDTO)
}