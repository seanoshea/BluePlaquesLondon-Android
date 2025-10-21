package com.upwardsnorthwards.blueplaqueslondon.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel

/**
 * Main Map Screen composable - displays Blue Plaques on an interactive map
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val plaques by viewModel.getPlaques().observeAsState(initial = null)
    val isLoading by viewModel.getLoading().observeAsState(initial = false)
    val error by viewModel.getError().observeAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blue Plaques London") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: $error",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Red
                        )
                    }
                }
                plaques != null -> {
                    MapContent(
                        plaquCount = plaques?.size ?: 0
                    )
                }
                else -> {
                    Text(
                        text = "Loading plaques...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun MapContent(plaquCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
    ) {
        // Placeholder for Google Maps - will be integrated with Compose Maps library
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Google Maps Integration")
            Text(text = "Displaying $plaquCount Blue Plaques")
        }
    }
}
