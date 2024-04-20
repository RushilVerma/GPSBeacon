package com.techcravers.gpsbeacon

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.*

@Composable
fun ShowDifference(selectedLocations: List<LocationItem>) {
    val difference = if (selectedLocations.size == 2) {
        val location1 = selectedLocations[0]
        val location2 = selectedLocations[1]

        val latitude1 = Math.toRadians(location1.latitude)
        val longitude1 = Math.toRadians(location1.longitude)
        val latitude2 = Math.toRadians(location2.latitude)
        val longitude2 = Math.toRadians(location2.longitude)

        val longitudeDifference = longitude2 - longitude1

        val y = sin(longitudeDifference) * cos(latitude2)
        val x = cos(latitude1) * sin(latitude2) - sin(latitude1) * cos(latitude2) * cos(longitudeDifference)
        val bearing = atan2(y, x)
        val bearingDegrees = Math.toDegrees(bearing)

        val compassDirections = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((bearingDegrees + 360) % 360 / 45).toInt()
        val direction = compassDirections[index]

        "Navigation : $direction"
    } else {
        "Select two locations to see the navigation direction"
    }

    Text(
        text = difference,
        modifier = Modifier.padding(16.dp)
    )
}
