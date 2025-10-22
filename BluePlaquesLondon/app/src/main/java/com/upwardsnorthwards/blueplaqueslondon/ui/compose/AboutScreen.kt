package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * About screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text("About") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Blue Plaques London",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Version 3.0",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Discover the fascinating history of London through the iconic blue plaques that mark the residences of notable figures.",
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Features",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            val features = listOf(
                "Interactive map of all Blue Plaques in London",
                "Search and filter plaques by name",
                "View detailed information about each plaque",
                "Find the nearest plaque to your location",
                "Access Wikipedia articles for notable figures",
                "Street View integration for location preview"
            )

            features.forEach { feature ->
                Text(
                    text = "• $feature",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Technology",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = "Built with modern Android technologies:\n" +
                        "• Jetpack Compose for UI\n" +
                        "• MVVM Architecture\n" +
                        "• Room Database\n" +
                        "• Hilt Dependency Injection\n" +
                        "• Navigation Component\n" +
                        "• RxJava3 for reactive programming\n" +
                        "• Firebase Crashlytics & Analytics",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
