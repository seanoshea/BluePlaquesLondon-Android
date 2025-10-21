package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upwardsnorthwards.blueplaqueslondon.data.preferences.AppPreferencesDataStore
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.Composable

/**
 * Compose-based main activity using Navigation Component with Jetpack Compose
 */
@AndroidEntryPoint
class ComposeActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var preferencesDataStore: AppPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.loadPlaques()

        setContent {
            ComposeApp(
                mainViewModel = mainViewModel,
                preferencesDataStore = preferencesDataStore,
                onPlacemarkSelected = { placemark ->
                    // Handle placemark selection
                    // Can launch detail view or show snackbar
                }
            )
        }
    }
}

@Composable
fun ComposeApp(
    mainViewModel: MainViewModel,
    preferencesDataStore: AppPreferencesDataStore,
    onPlacemarkSelected: (Placemark) -> Unit = {}
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        ComposeNavigation(
            navController = navController,
            mainViewModel = mainViewModel,
            preferencesDataStore = preferencesDataStore,
            onPlacemarkSelected = onPlacemarkSelected
        )
    }
}

@Composable
fun ComposeNavigation(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    preferencesDataStore: AppPreferencesDataStore,
    onPlacemarkSelected: (Placemark) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        composable("map") {
            MapScreen(
                viewModel = mainViewModel,
                onPlacemarkSelected = { placemark ->
                    onPlacemarkSelected(placemark)
                    navController.navigate("detail/${placemark.name ?: "plaque"}")
                },
                onNavigationMenuClick = {
                    // Show navigation menu with options
                }
            )
        }

        composable("detail/{plaqueName}") {
            DetailScreen(
                placemarks = emptyList(),
                onBackClick = {
                    navController.popBackStack()
                },
                onStreetViewClick = { placemark ->
                    navController.navigate("panorama/${placemark.name ?: "street_view"}")
                },
                onWikipediaClick = { placemark ->
                    navController.navigate("wikipedia/${placemark.name ?: "article"}")
                },
                onMultiplePlacemarksClick = {
                    // Will be handled by detail screen state
                }
            )
        }

        composable("wikipedia/{plaqueName}") {
            WikipediaScreen(
                placemark = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("panorama/{plaqueName}") {
            PanoramaScreen(
                placemark = null,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("about") {
            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                preferencesDataStore = preferencesDataStore,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
