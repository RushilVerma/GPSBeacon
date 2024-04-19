package com.techcravers.gpsbeacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LocationButton(onLocationSaved = { location ->
                        history.add("Location: $location")
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history)
                }
            }
        }
    }
}

@Composable
fun LocationButton(onLocationSaved: (String) -> Unit) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }

    Button(onClick = {
        // Get current location
        // For simplicity, assuming location is fetched and converted to a string
        val currentLocation = "Latitude: 40.7128, Longitude: -74.0060"
        location = currentLocation
        onLocationSaved(currentLocation)
    }) {
        Text(text = "Save Current Location")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "Current Location: $location")
}

@Composable
fun HistoryList(history: List<String>) {
    LazyColumn {
        items(history.size) { index ->
            Text(text = history[index])
        }
    }
}