package com.example.flightsearchapp.data

import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    fun getAllFlightSearchStream(): Flow<List<Airport>>

    fun getAllFavorites(): Flow<List<Favorite>>

    fun getAutoSuggestions(): Flow<List<Airport>>
}


class OfflineFlightSearchRepository(private val dao: FlightSearchDao): FlightSearchRepository {

    override fun getAllFlightSearchStream(): Flow<List<Airport>> = dao.getAllQueries()

    override fun getAllFavorites(): Flow<List<Favorite>> = dao.getAllFavorites()

    override fun getAutoSuggestions(): Flow<List<Airport>> = dao.getAutoSuggestions()

}