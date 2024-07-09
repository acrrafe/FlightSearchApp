package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {

//    @Insert
//    suspend fun addToFavorite()

//    @Delete
//    suspend fun deleteFavorite()

    @Transaction
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Transaction
    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE :query")
    fun getAllQueries(query: String): Flow<List<AirportWithPotentialFlights>>

    @Transaction
    @Query("SELECT * FROM airport")
    fun getAutoSuggestions(): Flow<List<Airport>>
}