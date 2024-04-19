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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationButton(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationSaved: (String) -> Unit
) {
    val context = LocalContext.current
    var location by remember { mutableStateOf("") }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
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
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.add_becon), // Your export icon drawable
                contentDescription = "Add Beacon Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Current Location: $location")
        }
    }
}

@Composable
fun ExportButton(context: Context, locationHistory: List<LocationItem>) {
    Card(modifier = Modifier
//        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            exportLocationHistory(context, locationHistory)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.export_icon), // Your export icon drawable
                contentDescription = "Export Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Export")
        }
    }
}

@Composable
fun ImportButton(context: Context, onImported: (List<LocationItem>) -> Unit) {
    Card(modifier = Modifier
//        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            val importedHistory = importLocationHistory(context)
            onImported(importedHistory)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.import_icon), // Your import icon drawable
                contentDescription = "Import Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Import")
        }
    }
}

fun exportLocationHistory(context: Context, locationHistory: List<LocationItem>) {
    val fileName = "location_history.csv"
    val filePath = File(context.getExternalFilesDir(null), fileName)
    val csvWriter = FileWriter(filePath)

    csvWriter.append("ID,Location\n")

    for (locationItem in locationHistory) {
        csvWriter.append("${locationItem.id},${locationItem.location}\n")
    }

    csvWriter.flush()
    csvWriter.close()
}

fun importLocationHistory(context: Context): List<LocationItem> {
    val fileName = "location_history.csv"
    val filePath = File(context.getExternalFilesDir(null), fileName)
    val locationHistory = mutableListOf<LocationItem>()

    if (filePath.exists()) {
        val csvReader = FileReader(filePath)
        val lines = csvReader.readLines()

        for (line in lines.drop(1)) { // Skip header line
            val columns = line.split(",")
            if (columns.size >= 2) {
                val id = columns[0].toInt()
                val location = columns[1]
                locationHistory.add(LocationItem(id, location))
            }
        }

        csvReader.close()
    }

    return locationHistory
}

@Composable
fun HistoryList(history: List<LocationItem>, selectedLocations: MutableList<LocationItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Location History",
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(history.size) { index ->
                val locationItem = history[index]
                LocationCard(locationItem = locationItem, isSelected = selectedLocations.contains(locationItem)) {
                    if (selectedLocations.size < 2) {
                        if (selectedLocations.contains(locationItem)) {
                            selectedLocations.remove(locationItem)
                        } else {
                            selectedLocations.add(locationItem)
                        }
                    } else {
                        selectedLocations.clear()
                        selectedLocations.add(locationItem)
                    }
                }
            }
        }
    }
}

@Composable
fun LocationCard(locationItem: LocationItem, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick.invoke() },
        color = if (isSelected) Color.Green else Color.White
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = locationItem.location
            )
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
