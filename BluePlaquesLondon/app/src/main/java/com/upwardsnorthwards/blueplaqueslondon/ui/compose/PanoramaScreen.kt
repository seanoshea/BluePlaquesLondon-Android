package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import com.google.android.gms.maps.model.LatLng
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text as Material3Text

/**
 * Panorama (Street View) screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanoramaScreen(
    placemark: Placemark? = null,
    onBackClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text(placemark?.name ?: "Street View") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (placemark != null && placemark.latitude != null && placemark.longitude != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            val streetViewUrl = "https://www.google.com/maps/@" +
                                "${placemark.latitude},${placemark.longitude},3a,75y,0h,90t/data=!3m4!1e1!3m2!1s0x0:0x0!2e0"
                            loadUrl(streetViewUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Material3Text(
                    text = "Street View not available for this location",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
