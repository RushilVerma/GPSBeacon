package com.techcravers.gpsbeacon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<String>()

    companion object {
        const val PERMISSION_REQUEST_CODE = 123 // Define your own request code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    LocationButton(fusedLocationClient, onLocationSaved = { location ->
                        history.add("Location: $location")
                    })
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationButton(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationSaved: (String) -> Unit
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
//    val lifecycleOwner = LocalLifecycleOwner.current

    Button(onClick = {
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
                    val currentLocation = "Latitude: $latitude, Longitude: $longitude"
                    location = currentLocation
                    onLocationSaved(currentLocation)
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                context as ComponentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.PERMISSION_REQUEST_CODE
            )
        }
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
            LocationCard(location = history[index])
        }
    }
}

@Composable
fun LocationCard(location: String) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
        Text(
            text = location,
            modifier = Modifier.padding(16.dp)
        )
    }
}
