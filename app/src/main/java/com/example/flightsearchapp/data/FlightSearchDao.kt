package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {

//    @Insert
//    suspend fun addToFavorite()

//    @Delete
//    suspend fun deleteFavorite()

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM airport")
    fun getAllQueries(): Flow<List<Airport>>

    @Query("SELECT * FROM airport")
    fun getAutoSuggestions(): Flow<List<Airport>>
}