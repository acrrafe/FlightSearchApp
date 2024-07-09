package com.example.flightsearchapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.relations.AirportWithPotentialFlights

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppBody(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory),
){
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
                viewModel.getFlight()
                viewModel.updateSearchStatus()
                       },
            active = data.value.active,
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
        LazyColumn {
            item {
                Text("Flight from ${data.value.userQuery}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp))
            }
            item {
                data.value.queriesFeedback.forEach { feedback ->
                    SearchResultCard(airportWithPotentialFlights = feedback, onItemClick = {})
                }
            }
//            item{
//                if(data.value.queriesFeedback.isEmpty()){
//                    Text("Size of queries: ${data.value.queriesFeedback.size}")
//                }else{
//                    Text("Size of queries: ${data.value.queriesFeedback[0]}")
//                }

//            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultCard(
    modifier: Modifier = Modifier,
    airportWithPotentialFlights: AirportWithPotentialFlights,
    onItemClick: (AirportWithPotentialFlights) -> Unit,
){
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(dimensionResource(id = R.dimen.card_elevation)),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius)),
        onClick = { onItemClick(airportWithPotentialFlights) },
    ){
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column (modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))){
                airportWithPotentialFlights.departuresAndDestination.forEach{
                    Text(text = "Depart")
                    Text(text = it.departureCode)
                    Text(text = "Arrive")
                    Text(text = it.destinationCode)

                }

            }
            Icon(imageVector = Icons.Default.Star, contentDescription = "Save")
        }
    }

}