package com.techcravers.gpsbeacon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HistoryList(history: List<LocationItem>, selectedLocations: MutableList<LocationItem>) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)) {
        Text(
            text = "Location History",
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Name", modifier = Modifier.weight(1f))
                    Divider(modifier = Modifier
                        .width(1.dp)
                        .background(color = Color.Black)) // Vertical divider
                    Text(text = "Latitude", modifier = Modifier.weight(1f))
                    Divider(modifier = Modifier
                        .width(1.dp)
                        .background(color = Color.Black)) // Vertical divider
                    Text(text = "Longitude", modifier = Modifier.weight(1f))
                    Divider(modifier = Modifier
                        .width(1.dp)
                        .background(color = Color.Black)) // Vertical divider
                    Text(text = "Altitude", modifier = Modifier.weight(1f))
                }
            }
            items(history.size) { index ->
                val locationItem = history[index]
                LocationCard(
                    locationItem = locationItem,
                    isSelected = selectedLocations.contains(locationItem))
                {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${locationItem.id}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
            Divider(modifier = Modifier.width(1.dp)) // Vertical divider
            Text(
                text = "${locationItem.latitude}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
            Divider(modifier = Modifier.width(1.dp)) // Vertical divider
            Text(
                text = "${locationItem.longitude}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
            Divider(modifier = Modifier.width(1.dp)) // Vertical divider
            Text(
                text = "${locationItem.altitude}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
        }
    }
}