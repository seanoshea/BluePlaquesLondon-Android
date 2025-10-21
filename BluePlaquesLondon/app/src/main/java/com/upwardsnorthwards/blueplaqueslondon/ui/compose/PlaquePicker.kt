package com.upwardsnorthwards.blueplaqueslondon.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark

/**
 * Composable dialog for selecting between multiple placemarks at the same location
 */
@Composable
fun PlaquePicker(
    placemarks: List<Placemark>,
    onPlacemarkSelected: (Placemark) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select a Plaque")
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                items(placemarks) { placemark ->
                    PlaquePagerItem(
                        placemark = placemark,
                        onClick = {
                            onPlacemarkSelected(placemark)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun PlaquePagerItem(
    placemark: Placemark,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        Text(
            text = placemark.getTrimmedName() ?: "Unknown",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        placemark.getTrimmedOccupation()?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
