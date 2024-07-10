package com.example.flightsearchapp.data.relations

import androidx.room.Embedded
import androidx.room.Relation

data class AirportWithPotentialFlights(
    @Embedded val potentialFlight: PotentialFlightEntity,
    @Relation(
        parentColumn = "departure_code",
        entityColumn = "iata_code"
    )
    val departureAirport: FlightSearchAirportEntity,
    @Relation(
        parentColumn = "destination_code",
        entityColumn = "iata_code"
    )
    val destinationAirport: FlightSearchAirportEntity
)
