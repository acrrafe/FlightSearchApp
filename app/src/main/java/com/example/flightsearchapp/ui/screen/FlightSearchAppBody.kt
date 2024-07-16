package com.example.flightsearchapp.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
    val data = viewModel.flightUiState.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        SearchBar(
            modifier = Modifier,
            query = data.value.userQuery,
            onQueryChange = {
                            viewModel.updateUserQuery(it)
                            viewModel.getAutoComplete(it)
            },
            onSearch = {
                viewModel.updateSearchStatus()
                viewModel.getPotentialFlights()

                       },
            active = data.value.isSearching,
            onActiveChange = {
                viewModel.updateSearchStatus()
                             },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon" )
            },
//            colors = SearchBarDefaults.colors(
//                containerColor = Color.Transparent,
//                dividerColor = Color.Transparent,
//                inputFieldColors = TextFieldDefaults.textFieldColors(
//                    containerColor = Color.Transparent,
//                    cursorColor = Color.Transparent,
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                )
//            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(
                    vertical = dimensionResource(id = R.dimen.padding_small),
                    horizontal = dimensionResource(id = R.dimen.padding_medium)
                ),
            ){
                item{
                    if (data.value.userQuery.isEmpty()){
                        data.value.suggestedAirports.forEach { airport ->
                            ClickableText( buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                    append("${airport.iata_code} ")
                                }
                                append(airport.name)
                            },
                                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                                onClick = {
                                    viewModel.getUserQueryAndPotentialFlights(airport.iata_code)
                                }
                            )
                        }
                    }else{
                        data.value.autoComplete.forEach { airport ->
                            ClickableText( buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                    append("${airport.iata_code} ")
                                }
                                append(airport.name)
                            },
                                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                                onClick = {
                                    viewModel.getUserQueryAndPotentialFlights(airport.iata_code)
                                }
                            )
                        }
                    }

                }
            }
        }
        FlightSearchList(
            modifier = Modifier.fillMaxSize(),
            flightUiState = data.value
        )
    }
}
