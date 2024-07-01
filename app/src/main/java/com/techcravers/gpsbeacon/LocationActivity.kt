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

class LocationActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<LocationItem>()
    private val selectedLocations = mutableStateListOf<LocationItem>()
    private val wifiDetails = mutableStateListOf<WifiNetworkItem>()
    private val currentLocation = mutableStateOf(LocationItem(-1, "Fetching location...", 0.0, 0.0, 0.0))
    private val showEditNameDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            LocationScreen()
        }
    }

    @Composable
    fun LocationScreen() {
        val context = LocalContext.current

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        LocationButton(fusedLocationClient, onLocationSaved = { location, latitude, longitude, altitude ->
                            history.add(LocationItem(history.size, location, latitude, longitude, altitude))
                        })
                        ExportButton(context = LocalContext.current, locationHistory = history, wifiNetworks = wifiDetails)
                        ImportButton(context = applicationContext) { importedHistory ->
                            history.clear()
                            history.addAll(importedHistory)
                        }
                        LocationDisplay(fusedLocationClient = fusedLocationClient, context = applicationContext, locationState = currentLocation)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ShowDifference(selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history, selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))
                    RefreshWifiNetworkList(context = applicationContext, wifiDetails = wifiDetails, currentLocation = currentLocation)
                }
                BottomNavigationBar(context = context, selectedTab = 1)
            }
        }
    }
}
