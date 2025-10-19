package com.upwardsnorthwards.blueplaqueslondon.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.upwardsnorthwards.blueplaqueslondon.R

@Composable
fun AboutScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.developed_details),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(id = R.string.developed_by),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(id = R.string.designer_details),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.designed_by),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(id = R.string.map_details),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(id = R.string.map_data),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
