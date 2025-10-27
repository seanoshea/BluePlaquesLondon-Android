package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel

/**
 * Main Map Screen composable using Jetpack Compose
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onPlacemarkSelected: (Placemark) -> Unit = {},
    onNavigationMenuClick: () -> Unit = {}
) {
    val plaques by viewModel.getPlaques().observeAsState(initial = emptyList())
    val isLoading by viewModel.getLoading().observeAsState(initial = false)
    val error by viewModel.getError().observeAsState(initial = null)

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar
            TopAppBar(
                title = { Text("Blue Plaques London") },
                navigationIcon = {
                    IconButton(onClick = onNavigationMenuClick) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchPlaques(it)
                },
                label = { Text("Search plaques...") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true
            )

            // Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error: $error",
                                color = Color.Red,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                    plaques != null && plaques!!.isNotEmpty() -> {
                        PlaqueLazyList(
                            plaques = plaques ?: emptyList(),
                            onPlacemarkClick = { placemark ->
                                viewModel.selectPlaque(placemark)
                                onPlacemarkSelected(placemark)
                            }
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No plaques available. Pull to refresh.")
                        }
                    }
                }
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}

@Composable
fun PlaqueLazyList(
    plaques: List<Placemark>,
    onPlacemarkClick: (Placemark) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        items(plaques) { placemark ->
            PlaqueListItem(
                placemark = placemark,
                onClick = { onPlacemarkClick(placemark) }
            )
        }
    }
}

@Composable
fun PlaqueListItem(
    placemark: Placemark,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = placemark.name ?: "Unknown",
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = placemark.title ?: "",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (!placemark.occupation.isNullOrEmpty()) {
            Text(
                text = "Occupation: ${placemark.occupation}",
                color = Color.Gray
            )
        }
    }
}
