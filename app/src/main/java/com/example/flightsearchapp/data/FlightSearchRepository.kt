package com.example.flightsearchapp.data

import androidx.room.Query
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    fun getAllFlightSearchStream(query: String): Flow<List<AirportWithFavorites>>
    fun getAllFlightSearchStream2(): Flow<List<Airport>>

    fun getAllFavorites(): Flow<List<Favorite>>

    fun getAutoSuggestions(): Flow<List<Airport>>
}


class OfflineFlightSearchRepository(private val dao: FlightSearchDao): FlightSearchRepository {

    override fun getAllFlightSearchStream(query: String): Flow<List<AirportWithFavorites>> = dao.getAllQueries(query)
    override fun getAllFlightSearchStream2(): Flow<List<Airport>> = dao.getAutoSuggestions()

    override fun getAllFavorites(): Flow<List<Favorite>> = dao.getAllFavorites()

    override fun getAutoSuggestions(): Flow<List<Airport>> = dao.getAutoSuggestions()

}