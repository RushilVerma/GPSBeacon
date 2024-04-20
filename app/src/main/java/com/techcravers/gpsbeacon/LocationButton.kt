package com.techcravers.gpsbeacon

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationButton(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationSaved: (String, Double, Double, Double) -> Unit
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editedLocation by remember { mutableStateOf("") }

    Card(modifier = Modifier
//        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationTask: Task<Location> = fusedLocationClient.lastLocation
                locationTask.addOnSuccessListener { locationResult ->
                    locationResult?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val altitude = it.altitude
                        val currentLocation = "Latitude: $latitude, Longitude: $longitude, Altitude: $altitude"
                        location = currentLocation
                        onLocationSaved(currentLocation, latitude, longitude, altitude)
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
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.add_becon), // Your export icon drawable
                contentDescription = "Add Beacon Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Add X")
        }
    }
}
