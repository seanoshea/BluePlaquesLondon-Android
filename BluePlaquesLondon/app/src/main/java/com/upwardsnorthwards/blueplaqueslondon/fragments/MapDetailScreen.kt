package com.upwardsnorthwards.blueplaqueslondon.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.upwardsnorthwards.blueplaqueslondon.R
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark

@Composable
fun MapDetailScreen(
    placemark: Placemark,
    onStreetViewClick: () -> Unit,
    onWikipediaClick: () -> Unit,
    onMoreClick: () -> Unit,
    isMoreButtonVisible: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = placemark.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = placemark.trimmedOccupation,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = placemark.address,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        placemark.councilAndYear?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        placemark.note?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            onClick = onStreetViewClick,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.street_view))
        }
        Button(
            onClick = onWikipediaClick,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.wikipedia_article))
        }
        if (isMoreButtonVisible) {
            Button(
                onClick = onMoreClick,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.multiple_placemarks))
            }
        }
    }
}
