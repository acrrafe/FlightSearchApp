package com.example.flightsearchapp.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.FlightSearchRepository
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import com.example.flightsearchapp.data.relations.FlightSearchFavoriteEntity
import com.example.flightsearchapp.model.Airport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class FlightSearchViewModel (private val flightSearchRepository: FlightSearchRepository) : ViewModel() {

    private val _flightUiState = MutableStateFlow(FlightUiState())
    val flightUiState: StateFlow<FlightUiState> = _flightUiState.asStateFlow()

    fun updateUserQuery(userQuery: String) {
        _flightUiState.update {
            it.copy(userQuery = userQuery)
        }
    }
    fun updateSearchStatus() {
        _flightUiState.update {
            it.copy(isSearching = !flightUiState.value.isSearching)
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            flightSearchRepository.getAllFavorites()
                .collect { favoriteFilteredList ->
                    _flightUiState.update {
                        it.copy(favoriteFlights = favoriteFilteredList)
                    }
                }
        }
    }

    private fun getSuggestions(){
        viewModelScope.launch {
            flightSearchRepository.getSuggestions()
                .collect{ airportSuggestions ->
                    _flightUiState.update {
                        it.copy(suggestedAirports = airportSuggestions)
                    }

                }
        }
    }

    fun getAutoComplete(query: String){
        viewModelScope.launch {
            flightSearchRepository.getAutoComplete(query)
                .collect{ airportSuggestions ->
                    _flightUiState.update {
                        it.copy(autoComplete = airportSuggestions)
                    }

                }
        }
    }


    fun getPotentialFlights(){
        viewModelScope.launch {
            flightSearchRepository.getAllFlightSearchStream(flightUiState.value.userQuery)
                .collect {airportFilteredList ->
                    _flightUiState.update {
                        it.copy(potentialFlights = airportFilteredList)

                    }

                }
        }
    }

    fun getUserQueryAndPotentialFlights(query: String){
        _flightUiState.update { it.copy(userQuery = query, isSearching = false) }
        getPotentialFlights()
        getAutoComplete(query)

    }

    suspend fun addToFavorite(departureCode : String, destinationCode : String){
        flightSearchRepository.addToFavorite(departureCode, destinationCode)
    }

    suspend fun deleteFavorite(departureCode: String, destinationCode: String){
        flightSearchRepository.deleteFavorite(departureCode, destinationCode)
    }

    fun isFavoriteFlight(departureCode: String, destinationCode: String): Boolean{
       return _flightUiState.value.favoriteFlights
               .any{ favorite ->
                   favorite.departureFavorite.iataCode == departureCode
                       && favorite.destinationFavorite.iataCode == destinationCode
           }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(application.container.flightSearchRepository)
            }
        }
    }

    init {
        getFavorites()
        getSuggestions()
    }
}

data class FlightUiState(
    val userQuery: String = "",
    val isSearching: Boolean = false,
    val suggestedAirports: List<Airport> = emptyList(),
    val autoComplete: List<Airport> = emptyList(),
    val potentialFlights: List<AirportWithPotentialFlights> = emptyList(),
    val favoriteFlights: List<FavoriteWithAirportAndPotentialFlights> = emptyList(),
    val isFavorite: Boolean = false
)


