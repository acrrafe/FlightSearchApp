package com.example.flightsearchapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class AirportWithFavorites(
    @Embedded val airport: FlightSearchAirportEntity,
    @Relation(
        parentColumn = "iata_code",
        entityColumn = "departure_code"
    )
    val departures: List<PotentialFlightEntity>,
//    @Relation(
//        parentColumn = "iata_code",
//        entityColumn = "destination_code"
//    )
//    val destinations: List<PotentialFlight>
)
