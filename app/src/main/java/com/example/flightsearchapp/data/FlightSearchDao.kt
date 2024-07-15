package com.example.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights

import com.example.flightsearchapp.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {


    @Query("""
        INSERT INTO favorite (departure_code, destination_code)
        SELECT :departureCode, :destinationCode
        WHERE NOT EXISTS (
            SELECT 1 
            FROM favorite 
            WHERE departure_code = :departureCode 
              AND destination_code = :destinationCode
        )
    """)
    suspend fun addToFavorite(departureCode: String, destinationCode: String)

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
    @Query("SELECT DISTINCT * FROM airport ORDER BY passengers LIMIT 10")
    fun getSuggestions(): Flow<List<Airport>>

    @Transaction
    @Query("SELECT * FROM airport WHERE name LIKE '%' || :query || '%' OR  iata_code LIKE '%' || :query || '%'")
    fun getAutoComplete(query: String): Flow<List<Airport>>
}