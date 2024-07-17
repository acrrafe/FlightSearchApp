package com.example.flightsearchapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R


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
                                    append("${airport.iataCode} ")
                                }
                                append(airport.name)
                            },
                                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                                onClick = {
                                    viewModel.getUserQueryAndPotentialFlights(airport.iataCode)
                                }
                            )
                        }
                    }else{
                        data.value.autoComplete.forEach { airport ->
                            ClickableText( buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)){
                                    append("${airport.iataCode} ")
                                }
                                append(airport.name)
                            },
                                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)),
                                onClick = {
                                    viewModel.getUserQueryAndPotentialFlights(airport.iataCode)
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
