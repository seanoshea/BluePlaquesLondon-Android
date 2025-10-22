package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark

/**
 * Detail screen composable showing plaque information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placemarks: List<Placemark> = emptyList(),
    onBackClick: () -> Unit = {},
    onStreetViewClick: (Placemark) -> Unit = {},
    onWikipediaClick: (Placemark) -> Unit = {},
    onMultiplePlacemarksClick: (List<Placemark>) -> Unit = {}
) {
    var currentPlacemark by remember {
        mutableStateOf(if (placemarks.isNotEmpty()) placemarks[0] else null)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text(currentPlacemark?.name ?: "Plaque Details") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (currentPlacemark != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Occupation
                currentPlacemark?.getTrimmedOccupation()?.let {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Address
                currentPlacemark?.address?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Council and Year
                currentPlacemark?.councilAndYear?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Note
                currentPlacemark?.note?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Street View Button
                Button(
                    onClick = { currentPlacemark?.let { onStreetViewClick(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Street View", color = Color.White)
                }

                // Wikipedia Button
                Button(
                    onClick = { currentPlacemark?.let { onWikipediaClick(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("Wikipedia Article", color = Color.White)
                }

                // Multiple Placemarks Button (only show if more than one)
                if (placemarks.size > 1) {
                    Button(
                        onClick = { onMultiplePlacemarksClick(placemarks) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("More Plaques Here", color = Color.White)
                    }
                }
            }
        }
    }
}
