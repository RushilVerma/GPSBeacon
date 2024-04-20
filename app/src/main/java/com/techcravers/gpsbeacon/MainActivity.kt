package com.techcravers.gpsbeacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

data class LocationItem(
    val id: Int,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double
)

data class WifiNetworkItem(
    val ssid: String,
    val rssi: Int,
    val location: LocationItem
)

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<LocationItem>()
    private val selectedLocations = mutableStateListOf<LocationItem>()
    private val wifiDetails = mutableStateListOf<WifiNetworkItem>()
    private val currentLocation = mutableStateOf(LocationItem(0, "Fetching location...", 0.0, 0.0, 0.0))
    companion object {
        const val PERMISSION_REQUEST_CODE = 123 // Define your own request code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        LocationButton(fusedLocationClient, onLocationSaved = { location, latitude, longitude, altitude ->
                            history.add(LocationItem(history.size, location, latitude, longitude, altitude))
                        })
//                        Spacer(modifier = Modifier.width(16.dp))
                        ExportButton(context = LocalContext.current, locationHistory = history, wifiNetworks = wifiDetails)
//                        Spacer(modifier = Modifier.width(8.dp))
                        ImportButton(context = applicationContext) { importedHistory ->
                            history.clear()
                            history.addAll(importedHistory)
                        }
//                        Spacer(modifier = Modifier.width(8.dp))
                        LocationDisplay(fusedLocationClient = fusedLocationClient, context = applicationContext, locationState = currentLocation)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ShowDifference(selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history, selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))
                    RefreshWifiNetworkList(context = applicationContext, wifiDetails = wifiDetails)

                }
            }
        }
    }
}