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

        val latitudeDifference = latitude2 - latitude1
        val longitudeDifference = longitude2 - longitude1

        val a = sin(latitudeDifference / 2).pow(2) + cos(latitude1) * cos(latitude2) * sin(longitudeDifference / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val earthRadius = 6371 // Radius of the Earth in kilometers
        val distance = earthRadius * c

        val heightDifference = location2.altitude - location1.altitude

        val y = sin(longitudeDifference) * cos(latitude2)
        val x = cos(latitude1) * sin(latitude2) - sin(latitude1) * cos(latitude2) * cos(longitudeDifference)
        val bearing = atan2(y, x)
        val bearingDegrees = Math.toDegrees(bearing)

        val compassDirections = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val index = ((bearingDegrees + 360) % 360 / 45).toInt()
        val direction = compassDirections[index]

        "Navigation from ${location1.location} to ${location2.location}: " +
                "Direction: $direction, Distance: ${"%.4f".format(distance)} km, Height Difference: $heightDifference meters"
    } else {
        "Select two locations to see the navigation details"
    }

    Text(
        text = difference,
        modifier = Modifier.padding(16.dp)
    )
}