package com.example.flightsearchapp.ui.screen

import android.Manifest
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearchapp.R
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchAppBody(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory),
){
    val data = viewModel.flightUiState.collectAsState()
    val context: Context = LocalContext.current

    val speechRecognizer = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK && result.data != null){
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let{
                viewModel.getUserQueryAndPotentialFlights(it)

            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getSpeechInput(context, speechRecognizer)
        } else {
            Toast.makeText(context, "Permission is denied, " +
                    "please turn it on manually in this application setting", Toast.LENGTH_SHORT).show()
        }
    }

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
            trailingIcon = {
                if(data.value.isSearching){
                    IconButton(onClick = { requestPermissionAndLaunchSpeechInput(context, permissionLauncher, speechRecognizer) },
                    ) {
                        Icon(painter = painterResource(id = R.drawable.searchbar_mic_24), contentDescription = "Mic")
                    }
                }
            }
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

private fun requestPermissionAndLaunchSpeechInput(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    speechRecognizerLauncher: ActivityResultLauncher<Intent>
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
        getSpeechInput(context, speechRecognizerLauncher)
    } else {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}

private fun getSpeechInput(context: Context, launcher: ActivityResultLauncher<Intent>){
    if(!SpeechRecognizer.isRecognitionAvailable(context)){
        Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
    }else{
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something")
        }
        launcher.launch(intent)
    }
}



