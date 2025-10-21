package com.upwardsnorthwards.blueplaqueslondon.ui.screens

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.SettingsViewModel

/**
 * Settings Screen composable - displays app settings and preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val analyticsEnabled by viewModel.getAnalyticsEnabled().observeAsState(initial = true)
    val launchCount by viewModel.getLaunchCount().observeAsState(initial = 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy",
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingItem(
                title = "Analytics",
                description = "Help improve the app by sending usage analytics",
                isEnabled = analyticsEnabled,
                onToggle = { viewModel.setAnalyticsEnabled(it) }
            )

            Text(
                text = "App Information",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            SettingItemInfo(
                title = "App Version",
                value = "3.0"
            )

            SettingItemInfo(
                title = "Times Launched",
                value = launchCount.toString()
            )

            Text(
                text = "About",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )

            Text(
                text = "Blue Plaques London\n\nDiscover the fascinating history of London through interactive maps and detailed information about historic blue plaques.\n\nBuilt with modern Android technologies including Jetpack Compose, Room Database, Hilt DI, and Navigation Component.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}

@Composable
fun SettingItemInfo(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
