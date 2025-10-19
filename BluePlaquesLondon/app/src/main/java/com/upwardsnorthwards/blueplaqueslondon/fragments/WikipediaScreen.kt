package com.upwardsnorthwards.blueplaqueslondon.fragments

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WikipediaScreen(url: String) {
    val isLoading = remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                WebView(it).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading.value = false
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
