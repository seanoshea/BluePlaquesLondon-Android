package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.util.Log
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark

/**
 * Wikipedia screen composable for displaying web content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikipediaScreen(
    placemark: Placemark? = null,
    onBackClick: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text(placemark?.name ?: "Wikipedia") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (placemark != null) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    isLoading = newProgress < 100
                                }
                            }
                        }
                    },
                    update = { webView ->
                        placemark.getTrimmedName()?.let { name ->
                            val searchUrl = "https://en.wikipedia.org/wiki/${name.replace(" ", "_")}"
                            webView.loadUrl(searchUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
