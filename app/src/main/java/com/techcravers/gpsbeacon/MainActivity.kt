package com.techcravers.gpsbeacon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.io.File
import java.io.FileWriter
import java.io.FileReader

data class LocationItem(val id: Int, val location: String)

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val history = mutableStateListOf<LocationItem>()
    private val selectedLocations = mutableStateListOf<LocationItem>()

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
                        history.add(LocationItem(history.size, location))
                    })
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ExportButton(context = applicationContext, locationHistory = history)
                        Spacer(modifier = Modifier.width(16.dp))
                        ImportButton(context = applicationContext) { importedHistory ->
                            history.clear()
                            history.addAll(importedHistory)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ShowDifference(selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))
                    HistoryList(history = history, selectedLocations = selectedLocations)
                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}

@Composable
fun ShowDifference(selectedLocations: List<LocationItem>) {
    val difference = if (selectedLocations.size == 2) {
        "Difference: ${selectedLocations[0].location} - ${selectedLocations[1].location}"
    } else {
        "Select two locations to see the difference"
    }

    Text(
        text = difference,
        modifier = Modifier.padding(16.dp)
    )
}
