package com.example.flightsearchapp.data.relations

import androidx.room.Embedded
import androidx.room.Relation

data class AirportWithPotentialFlights(
    @Embedded val airport: FlightSearchAirportEntity,
    @Relation(
        parentColumn = "iata_code",
        entityColumn = "departure_code"
    )
    val departuresAndDestination: List<PotentialFlightEntity>,
)