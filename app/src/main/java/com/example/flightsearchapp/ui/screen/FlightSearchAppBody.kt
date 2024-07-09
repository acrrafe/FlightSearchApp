package com.example.flightsearchapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
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
        modifier = modifier,
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
            }
        ) {
            // TODO: Implement AutoSuggest Using DataSharedPreference

        }
        // TODO: Implement Queries Result Layout
        LazyColumn {
            item {
                Text("Queries Feedback:", fontWeight = FontWeight.Bold)
            }
            item {
                data.value.queriesFeedback.forEach { feedback ->
                    list.add(feedback)
                }
            }
            item{
                if(data.value.queriesFeedback.isEmpty()){
                    Text("Size of queries: ${data.value.queriesFeedback.size}")
                }else{
                    Text("Size of queries: ${data.value.queriesFeedback[0]}")
                }

            }
        }
    }
}