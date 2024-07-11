package com.example.flightsearchapp.data.relations

import androidx.room.Embedded
import androidx.room.Relation

data class FavoriteWithAirportAndPotentialFlights(
    @Embedded val favorite: FlightSearchFavoriteEntity,
    @Relation(
        parentColumn = "departure_code",
        entityColumn = "iata_code"
    )
    val departuresFavorite: FlightSearchAirportEntity,
    @Relation(
        parentColumn = "destination_code",
        entityColumn = "iata_code"
    )
    val destinationFavorite: FlightSearchAirportEntity
)
