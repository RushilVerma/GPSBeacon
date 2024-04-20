package com.techcravers.gpsbeacon

import android.content.Intent
import android.net.Uri
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import java.io.File
import java.io.FileWriter
import androidx.core.content.FileProvider
@Composable
fun ExportButton(context: Context, locationHistory: List<LocationItem>, wifiNetworks: List<WifiNetworkItem>) {
    Card(modifier = Modifier
//        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            exportLocationHistory(context, locationHistory, wifiNetworks)
        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.export_icon), // Your export icon drawable
                contentDescription = "Export Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Export")
        }
    }
}

fun exportLocationHistory(context: Context, locationHistory: List<LocationItem>, wifiNetworks: List<WifiNetworkItem>) {
    val fileName = "location_history.csv"
    val filePath = File(context.getExternalFilesDir(null), fileName)
    Log.d("GPSBeacon", "exportLocationHistory: Export Path $filePath")
    try {
        val csvWriter = FileWriter(filePath)

        // Write header for location data
        csvWriter.append("ID,Location,Latitude,Longitude,Altitude\n")

        // Write location data
        for (locationItem in locationHistory) {
            csvWriter.append("${locationItem.id},${locationItem.location},${locationItem.latitude},${locationItem.longitude},${locationItem.altitude}\n")
        }

        // Write header for Wi-Fi data
        csvWriter.append("\nSSID,RSSI,Location\n")

        // Write Wi-Fi data
        for (wifiNetwork in wifiNetworks) {
            csvWriter.append("${wifiNetwork.ssid},${wifiNetwork.rssi},${wifiNetwork.location.location}\n")
        }

        csvWriter.flush()
        csvWriter.close()

        // Share the CSV file
        val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", filePath)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Add flag to grant read permission
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share CSV File"))
    } catch (e: Exception) {
        // Handle exceptions
        e.printStackTrace()
    }
}