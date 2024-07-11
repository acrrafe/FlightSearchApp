package com.example.flightsearchapp.data

import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import com.example.flightsearchapp.data.relations.FlightSearchFavoriteEntity
import com.example.flightsearchapp.model.Airport
import com.example.flightsearchapp.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    suspend fun addToFavorite(favorite: FlightSearchFavoriteEntity)
    fun getAllFlightSearchStream(query: String): Flow<List<AirportWithPotentialFlights>>

    fun getAllFavorites(): Flow<List<FavoriteWithAirportAndPotentialFlights>>

    fun getAutoSuggestions(): Flow<List<Airport>>
}


class OfflineFlightSearchRepository(private val dao: FlightSearchDao): FlightSearchRepository {
    override suspend fun addToFavorite(favorite: FlightSearchFavoriteEntity)  = dao.addToFavorite(favorite)

    override fun getAllFlightSearchStream(query: String): Flow<List<AirportWithPotentialFlights>> = dao.getAllQueries(query)

    override fun getAllFavorites(): Flow<List<FavoriteWithAirportAndPotentialFlights>> = dao.getAllFavorites()

    override fun getAutoSuggestions(): Flow<List<Airport>> = dao.getAutoSuggestions()

}