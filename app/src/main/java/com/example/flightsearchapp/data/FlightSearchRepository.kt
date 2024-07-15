package com.example.flightsearchapp.data

import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import com.example.flightsearchapp.model.Airport
import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {

    suspend fun addToFavorite(departureCode: String, destinationCode: String)

    suspend fun deleteFavorite(departureCode: String, destinationCode: String)
    fun getAllFlightSearchStream(query: String): Flow<List<AirportWithPotentialFlights>>

    fun getAllFavorites(): Flow<List<FavoriteWithAirportAndPotentialFlights>>

    fun getSuggestions(): Flow<List<Airport>>
    fun getAutoComplete(query: String): Flow<List<Airport>>
}


class OfflineFlightSearchRepository(private val dao: FlightSearchDao): FlightSearchRepository {
    override suspend fun addToFavorite(departureCode: String, destinationCode: String)  = dao.addToFavorite(departureCode, destinationCode)

    override suspend fun deleteFavorite(departureCode: String, destinationCode: String) = dao.deleteFavorite(departureCode, destinationCode)

    override fun getAllFlightSearchStream(query: String): Flow<List<AirportWithPotentialFlights>> = dao.getAllQueries(query)

    override fun getAllFavorites(): Flow<List<FavoriteWithAirportAndPotentialFlights>> = dao.getAllFavorites()

    override fun getSuggestions(): Flow<List<Airport>> = dao.getSuggestions()

    override fun getAutoComplete(query: String): Flow<List<Airport>> = dao.getAutoComplete(query)

}