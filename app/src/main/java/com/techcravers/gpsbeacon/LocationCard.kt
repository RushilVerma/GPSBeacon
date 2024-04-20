package com.techcravers.gpsbeacon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.delay
import kotlin.random.Random
@Composable
fun LocationDisplay(fusedLocationClient: FusedLocationProviderClient, context: Context) {
    var currentLocation by remember { mutableStateOf("Fetching location...") }
    var cardColor by remember { mutableStateOf(Color.Cyan) }

    LaunchedEffect(Unit) {
        while (true) {
            refreshLocation(context, fusedLocationClient) { location ->
                currentLocation = location
                cardColor = generateRandomColor()
            }
            delay(1000) // Delay for 1 second
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(cardColor)
//            .padding(16.dp),
//        elevation = 4.dp
    ) {
        Text(text = currentLocation)
    }
}

private fun generateRandomColor(): Color {
    val random = Random.Default
    val red = random.nextInt(256)
    val green = random.nextInt(256)
    val blue = random.nextInt(256)
    return Color(red, green, blue)
}

private fun refreshLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (String) -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val locationTask: Task<android.location.Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener { locationResult ->
            locationResult?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                val altitude = it.altitude
                val currentLocation = "Latitude: $latitude, Longitude: $longitude, Altitude : $altitude"
                onLocationFetched(currentLocation)
            }
        }
    } else {
        ActivityCompat.requestPermissions(
            context as ComponentActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MainActivity.PERMISSION_REQUEST_CODE
        )
    }
}
