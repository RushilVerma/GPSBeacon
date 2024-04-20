package com.techcravers.gpsbeacon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.delay
import androidx.compose.foundation.border
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun WifiListItems(network: WifiNetworkItem) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.Gray) // Add border for separation
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${network.ssid}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
            Divider(modifier = Modifier.width(1.dp)) // Vertical divider
            Text(
                text = "${network.rssi}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
            Divider(modifier = Modifier.width(1.dp)) // Vertical divider
            Text(
                text = "${network.location.location}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center // Center-align the text
            )
        }
    }
}

@Composable
fun WifiNetworkList(NetworkLists: List<WifiNetworkItem>) {
    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "SSID", modifier = Modifier.weight(1f))
                Divider(modifier = Modifier.width(1.dp)) // Vertical divider
                Text(text = "RSSI Strength(dBm)", modifier = Modifier.weight(1f))
                Divider(modifier = Modifier.width(1.dp)) // Vertical divider
                Text(text = "Location", modifier = Modifier.weight(1f))
            }
        }
        items(NetworkLists) { network ->
            WifiListItems(network = network)
        }
    }
}

@Composable
fun RefreshWifiNetworkList(
    context: Context,
    wifiDetails: MutableList<WifiNetworkItem>,
    currentLocation: MutableState<LocationItem>
) {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    LaunchedEffect(Unit) {
        val delayMillis = 1000L // 1 second
        while (true) {
            if (wifiManager != null) {
                val networks = scanWifiNetworks(context, wifiManager)
                val topNetworks = networks.sortedByDescending { it.level }
                topNetworks.forEach { network ->
                    val existingNetwork = wifiDetails.find { it.ssid == network.SSID }
                    if (existingNetwork != null) {
                        if (existingNetwork.location.id == -1 || existingNetwork.rssi < network.level) {
                            existingNetwork.location = currentLocation.value // Update location
                        }
                    } else {
                        wifiDetails.add(
                            WifiNetworkItem(
                                ssid = network.SSID,
                                rssi = network.level,
                                location = currentLocation.value
                            )
                        )
                    }
                }
            }
            delay(delayMillis)
        }
    }

    WifiNetworkList(NetworkLists = wifiDetails)
}
fun scanWifiNetworks(context: Context, wifiManager: WifiManager?): List<ScanResult> {
    val permissionsGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!permissionsGranted) {
        // Request location permission if not granted
        ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        return emptyList()
    }

    return wifiManager?.scanResults ?: emptyList()
}

private const val PERMISSION_REQUEST_CODE = 111 // Define your own request code