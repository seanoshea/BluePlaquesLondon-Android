package com.upwardsnorthwards.blueplaqueslondon.fragments

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.FirebaseAnalytics
import com.upwardsnorthwards.blueplaqueslondon.R
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val analyticsEnabled = remember {
        mutableStateOf(BluePlaquesSharedPreferences.getAnalyticsEnabled(context))
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.analytics),
            style = MaterialTheme.typography.headlineSmall
        )
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = stringResource(id = R.string.analytics_title),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = analyticsEnabled.value,
                onCheckedChange = {
                    analyticsEnabled.value = it
                    FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(it)
                    BluePlaquesSharedPreferences.saveAnalyticsEnabled(context, it)
                }
            )
        }
        Text(
            text = stringResource(id = R.string.analytics_note),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
