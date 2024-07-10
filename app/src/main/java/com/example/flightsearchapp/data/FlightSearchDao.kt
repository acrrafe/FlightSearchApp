package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FlightSearchFavoriteEntity
import com.example.flightsearchapp.data.relations.PotentialFlightEntity

import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {

    @Insert
    suspend fun addToFavorite(favorite: FlightSearchFavoriteEntity)

//    @Delete
//    suspend fun deleteFavorite()

    @Transaction
    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Transaction
    @Query("""
        SELECT * FROM potential_flights
        WHERE departure_code IN (SELECT iata_code FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%')
           OR destination_code IN (SELECT iata_code FROM airport WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%')
    """)
    fun getAllQueries(query: String): Flow<List<AirportWithPotentialFlights>>

    @Transaction
    @Query("SELECT * FROM airport")
    fun getAutoSuggestions(): Flow<List<Airport>>
}