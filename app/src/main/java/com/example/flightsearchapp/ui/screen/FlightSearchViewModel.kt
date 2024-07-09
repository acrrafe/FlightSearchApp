package com.example.flightsearchapp.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.FlightSearchRepository
import com.example.flightsearchapp.model.Airport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
            it.copy(active = !flightUiState.value.active)
        }
    }
    fun getFlight(){
        viewModelScope.launch {
            flightSearchRepository.getAllFlightSearchStream(flightUiState.value.userQuery)
                .collect {airportFilteredList ->
                    _flightUiState.update {
                        it.copy(queriesFeedback = airportFilteredList)

                    }

                }

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
}


data class FlightUiState(
    val userQuery: String = "",
    val active: Boolean = false,
    val queriesFeedback: List<AirportWithPotentialFlights> = emptyList()
)