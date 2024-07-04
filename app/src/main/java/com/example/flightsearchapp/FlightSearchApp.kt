package com.example.flightsearchapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.ui.screen.FlightSearchAppBody


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchApp(
    modifier: Modifier = Modifier
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FlightTopAppBar(
                scrollBehavior = scrollBehavior,
                canNavigateBack = false,
                onBack = {}
            )
        }
    ) { innerPadding ->
        Surface (modifier = Modifier.fillMaxSize()){
            FlightSearchAppBody(
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    canNavigateBack: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
){
    CenterAlignedTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        },

//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = Color.Transparent,
//            titleContentColor = MaterialTheme.colorScheme.primary,
//            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//            actionIconContentColor = MaterialTheme.colorScheme.onSecondary
//        ),
        navigationIcon = {
            if(canNavigateBack){
                IconButton(onClick = onBack ) {
                    Icon(imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        modifier = modifier.shadow(elevation = 5.dp)

    )
}


