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
import com.example.flightsearchapp.data.relations.FlightSearchFavoriteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlightSearchViewModel (private val flightSearchRepository: FlightSearchRepository) : ViewModel() {

    private val _flightUiState = MutableStateFlow(FlightUiState())
    val flightUiState: StateFlow<FlightUiState> = _flightUiState

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

//    fun getFavorites(): StateFlow<FlightUiState> =
//        flightSearchRepository.getAllFavorites()
//            .filterNotNull()
//            .map { FlightUiState(favoriteFlights =  it) }
//            .stateIn(
//                viewModelScope,
//                started = SharingStarted.WhileSubscribed(5_000L),
//                initialValue = FlightUiState()
//            )

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

    suspend fun addToFavorite(departureCode : String, destinationCode : String){
        flightSearchRepository.addToFavorite(
            favorite = FlightSearchFavoriteEntity(0, departureCode, destinationCode)
        )
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
    }
}


data class FlightUiState(
    val userQuery: String = "",
    val isSearching: Boolean = false,
    val potentialFlights: List<AirportWithPotentialFlights> = emptyList(),
    val favoriteFlights: List<FavoriteWithAirportAndPotentialFlights> = emptyList(),
    val isFavorite: Boolean = false
)


