package com.example.flightsearchapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights
import com.example.flightsearchapp.data.relations.FavoriteWithAirportAndPotentialFlights
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppBody(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory),
){
    val coroutineScope = rememberCoroutineScope()
    val data = viewModel.flightUiState.collectAsState()
    var list = mutableListOf<AirportWithPotentialFlights>()

    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
    ){
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = data.value.userQuery,
            onQueryChange = viewModel::updateUserQuery,
            onSearch = {
                viewModel.getPotentialFlights()
                viewModel.updateSearchStatus()
                       },
            active = data.value.isSearching,
            onActiveChange = {
                viewModel.updateSearchStatus()
                             },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon" )
            },
        ) {
            // TODO: Implement AutoSuggest Using DataSharedPreference

        }
        // TODO: Implement Queries Result Layout
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            if(data.value.userQuery.isNotEmpty()){
                item {
                    Text("Flight from ${data.value.userQuery}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp))
                }
                item {
                    data.value.potentialFlights.forEach { feedback ->

                        val isFavorite: Boolean = viewModel.isFavoriteFlight(
                            feedback.departureAirport.iataCode,
                            feedback.destinationAirport.iataCode
                        )
                        SearchResultCard(
                            isFavorite = isFavorite,
                            airportWithPotentialFlights = feedback,
                            onItemClick = {
                                coroutineScope.launch {
                                    if(isFavorite){
                                        viewModel.deleteFavorite(
                                            feedback.departureAirport.iataCode,
                                            feedback.destinationAirport.iataCode
                                        )
                                    }else{
                                        viewModel.addToFavorite(
                                            feedback.departureAirport.iataCode,
                                            feedback.destinationAirport.iataCode
                                        )
                                    }

                                 }
                            })
                    }
                }
            }else{
                item {
                    Text("Favorite routes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp))
                }
                item {
                    viewModel.getFavorites()
                    Log.d("MAINACT", data.value.favoriteFlights.toString())
                    data.value.favoriteFlights.forEach { feedback ->
                        FavoriteCard(
                            favoriteWithAirportAndPotentialFlights = feedback,
                            onItemClick = {
                                coroutineScope.launch {
                                    viewModel.deleteFavorite(
                                        feedback.departureFavorite.iataCode,
                                        feedback.destinationFavorite.iataCode)
                                }
                            })

                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultCard(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    airportWithPotentialFlights: AirportWithPotentialFlights,
    onItemClick: () -> Unit,
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.card_elevation)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        onClick = {},
    ){
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Column (modifier = Modifier
                .weight(1f)){
                Text(text = "Depart")
                Text( buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                        append("${airportWithPotentialFlights.departureAirport.iataCode} ")
                    }
                    append(airportWithPotentialFlights.departureAirport.name)
                  }
                )
                Text(text = "Arrive")
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                        append("${airportWithPotentialFlights.destinationAirport.iataCode} ")
                    }
                    append(airportWithPotentialFlights.destinationAirport.name)
                }
                )

            }
            IconButton(onClick = onItemClick) {
                Icon(imageVector = Icons.Outlined.Star,
                    contentDescription = "Save",
                    modifier = Modifier.size(48.dp),
                    tint = if(isFavorite) Color(0xFFFFD700) else Color.Gray
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCard(
    modifier: Modifier = Modifier,
    favoriteWithAirportAndPotentialFlights: FavoriteWithAirportAndPotentialFlights,
    onItemClick: () -> Unit,
){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
        elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.card_elevation)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        onClick = {},
    ){
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Column (modifier = Modifier
                .weight(1f)){
                Text(text = "Depart")
                Text( buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                        append("${favoriteWithAirportAndPotentialFlights.departureFavorite.iataCode} ")
                    }
                    append(favoriteWithAirportAndPotentialFlights.departureFavorite.name)
                }
                )
                Text(text = "Arrive")
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                        append("${favoriteWithAirportAndPotentialFlights.destinationFavorite.iataCode} ")
                    }
                    append(favoriteWithAirportAndPotentialFlights.destinationFavorite.name)
                }
                )

            }
            IconButton( onClick = onItemClick) {
                Icon(imageVector = Icons.Outlined.Star,
                    contentDescription = "Save",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFD700)
                )
            }
        }
    }

}