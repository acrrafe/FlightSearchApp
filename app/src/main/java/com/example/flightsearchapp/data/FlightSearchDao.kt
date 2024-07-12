package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import com.example.flightsearchapp.data.relations.FlightSearchFavoriteEntity
import com.example.flightsearchapp.data.relations.PotentialFlightEntity

import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {


    suspend fun addToFavorite(favorite: FlightSearchFavoriteEntity)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode ")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Transaction
    @Query("""
       SELECT f.*
       FROM favorite f
       JOIN airport a1 ON f.departure_code = a1.iata_code
       JOIN airport a2 ON f.destination_code = a2.iata_code
       ORDER BY a1.passengers DESC, a2.passengers DESC
        """)
    fun getAllFavorites(): Flow<List<FavoriteWithAirportAndPotentialFlights>>

    @Transaction
    @Query("""
        SELECT p.*
        FROM potential_flights p
        JOIN airport a1 ON p.departure_code = a1.iata_code
        JOIN airport a2 ON p.destination_code = a2.iata_code 
        WHERE a1.name LIKE '%' || :query || '%' OR a1.iata_code LIKE '%' || :query || '%'
        OR a2.name LIKE '%' || :query || '%' OR a2.iata_code LIKE '%' || :query || '%'
        ORDER BY a1.passengers DESC, a2.passengers DESC
    """)
    fun getAllQueries(query: String): Flow<List<AirportWithPotentialFlights>>

    @Transaction
    @Query("SELECT * FROM airport")
    fun getAutoSuggestions(): Flow<List<Airport>>
}