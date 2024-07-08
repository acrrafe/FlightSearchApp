package com.example.flightsearchapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.data.AirportWithFavorites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppBody(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
){
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false)  }
    val data = viewModel.flightUiState.collectAsState()
    val sample = viewModel.getAllAirport().collectAsState()
    var list = mutableListOf<AirportWithFavorites>()

    Column(
        modifier = modifier,
    ){
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            query = data.value.userQuery,
            onQueryChange = viewModel::updateUserQuery,
            onSearch = {
                data.value.active
                viewModel.getFlight()
                       },
            active = data.value.active,
            onActiveChange = {viewModel.updateSearchStatus()},
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon" )
            }
        ) {
            // Queries Feedback Section

             Column {
                 Text("Queries Feedback:", fontWeight = FontWeight.Bold)
                 data.value.queriesFeedback.forEach { feedback ->
                     list.add(feedback)
                 }
                 Text("Size of queries: ${data.value.queriesFeedback.size}")
             }


        }

        Text("Size of queries: ${data.value.userQuery}")
        Text("Size of queries: ${data.value.active}")
    }
}