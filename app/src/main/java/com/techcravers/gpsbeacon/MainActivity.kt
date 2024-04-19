package com.techcravers.gpsbeacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<String>()
    private var isLocationPermissionGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LocationButton(
                        onLocationSaved = { location ->
                            history.add("Location: $location")
                        },
                        onPermissionDenied = {
                            // Handle permission denied
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history)
                    Spacer(modifier = Modifier.height(16.dp))
                    LocationTextBox(isLocationPermissionGranted)
                }
            }
        }

        // Check and request location permission
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}

@Composable
fun LocationButton(
    onLocationSaved: (String) -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var location by remember { mutableStateOf("") }

    Button(onClick = {
        // Get current location if permission is granted
        if (checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            getLocation(context) { currentLocation ->
                location = currentLocation
                onLocationSaved(currentLocation)
            }
        } else {
            onPermissionDenied()
        }
    }) {
        Text(text = "Save Current Location")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(text = "Current Location: $location")
}

@SuppressLint("MissingPermission")
fun getLocation(context: Context, onLocationResult: (String) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val currentLocation = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
            onLocationResult(currentLocation)
        } else {
            onLocationResult("Location not available")
        }
    }
}

@Composable
fun LocationTextBox(isLocationPermissionGranted: Boolean) {
    var realLocation by remember { mutableStateOf("") }

    LaunchedEffect(isLocationPermissionGranted) {
        if (isLocationPermissionGranted) {
            // Request location
            getLocation(LocalContext.current) { location ->
                realLocation = "Real Location: $location"
            }
        } else {
            realLocation = "Location permission not granted"
        }
    }

    Text(text = realLocation)
}

@Composable
fun HistoryList(history: List<String>) {
    LazyColumn {
        items(history.size) { index ->
            Text(text = history[index])
        }
    }
}