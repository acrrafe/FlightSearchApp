package com.example.flightsearchapp.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.FlightSearchRepository
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import com.example.flightsearchapp.data.relations.FlightSearchAirportEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlightSearchViewModel (private val flightSearchRepository: FlightSearchRepository) : ViewModel() {

    private val _flightUiState = MutableStateFlow(FlightUiState())
    val flightUiState: StateFlow<FlightUiState> = _flightUiState.asStateFlow()

    /**
     * Get the favorite list of flights of the user
     */
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

    /**
     * List of Functions for Search Bar
     */
    fun updateUserQuery(userQuery: String) {
        _flightUiState.update {
            it.copy(userQuery = userQuery)
        }
    }
    // This function is called  to track the state of our SearchBar
    fun updateSearchStatus() {
        _flightUiState.update {
            it.copy(isSearching = !flightUiState.value.isSearching)
        }
    }
    // Provide a limited set of flights as initial recommendation
    private fun getInitialSuggestions(){
        viewModelScope.launch {
            flightSearchRepository.getSuggestions()
                .collect{ airportSuggestions ->
                    _flightUiState.update {
                        it.copy(suggestedAirports = airportSuggestions)
                    }

                }
        }
    }
    // This function is called whenever the user updates our search query
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
    // This function is called when the user trigger's the search button of SearchBar
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
    // This function is called when the user clicks the suggestion in our SearchBar
    fun getUserQueryAndPotentialFlights(query: String){
        _flightUiState.update { it.copy(userQuery = query, isSearching = false) }
        getPotentialFlights()
        getAutoComplete(query)

    }

    /**
     * List of our database functions to Add, Delete, and Check if Something is already
     * existing in our database
     */
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
        getInitialSuggestions()
    }
}

data class FlightUiState(
    val userQuery: String = "",
    val isSearching: Boolean = false,
    val suggestedAirports: List<FlightSearchAirportEntity> = emptyList(),
    val autoComplete: List<FlightSearchAirportEntity> = emptyList(),
    val potentialFlights: List<AirportWithPotentialFlights> = emptyList(),
    val favoriteFlights: List<FavoriteWithAirportAndPotentialFlights> = emptyList(),
    val isFavorite: Boolean = false
)


