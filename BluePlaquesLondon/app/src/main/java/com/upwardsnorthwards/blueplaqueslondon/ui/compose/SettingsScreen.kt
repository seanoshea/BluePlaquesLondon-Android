package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwardsnorthwards.blueplaqueslondon.data.preferences.AppPreferencesDataStore
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Settings screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesDataStore: AppPreferencesDataStore,
    onBackClick: () -> Unit = {}
) {
    var localAnalyticsEnabled by remember { mutableStateOf(true) }
    var launchCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        preferencesDataStore.getAnalyticsEnabled()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { enabled ->
                localAnalyticsEnabled = enabled
            }

        preferencesDataStore.getLaunchCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { count ->
                launchCount = count
            }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = { Text("Settings") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy & Analytics",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                    Text(text = "Analytics", fontSize = 16.sp)
                    Text(
                        text = "Help improve the app by sending usage data",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = localAnalyticsEnabled,
                    onCheckedChange = {
                        localAnalyticsEnabled = it
                        preferencesDataStore.saveAnalyticsEnabled(it)
                    }
                )
            }

            Text(
                text = "App Information",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            SettingsItem(title = "Version", value = "3.0")
            SettingsItem(title = "App Times Launched", value = launchCount.toString())

            Text(
                text = "About",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            Text(
                text = "Blue Plaques London\n\n" +
                        "Discover historic blue plaques across London with this modern Android app " +
                        "built using Jetpack Compose, Room Database, MVVM architecture, and Hilt DI.\n\n" +
                        "© 2024 Upwards Northwards Software Limited",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SettingsItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp)
        }
        Text(text = value, fontSize = 14.sp, color = Color.Gray)
    }
}
