package com.techcravers.gpsbeacon

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun HistoryList(history: List<LocationItem>, selectedLocations: MutableList<LocationItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Location History",
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(history.size) { index ->
                val locationItem = history[index]
                LocationCard(locationItem = locationItem, isSelected = selectedLocations.contains(locationItem)) {
                    if (selectedLocations.size < 2) {
                        if (selectedLocations.contains(locationItem)) {
                            selectedLocations.remove(locationItem)
                        } else {
                            selectedLocations.add(locationItem)
                        }
                    } else {
                        selectedLocations.clear()
                        selectedLocations.add(locationItem)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationCard(locationItem: LocationItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(5.dp)
            .clickable { onClick.invoke() },
        color = if (isSelected) Color.Green else Color.White
    ) {

            Text(
                text = locationItem.location,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )

    }
}