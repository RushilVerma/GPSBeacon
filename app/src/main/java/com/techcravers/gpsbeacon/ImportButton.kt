package com.techcravers.gpsbeacon

import android.content.Context
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
import java.io.File
import java.io.FileReader

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
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = R.drawable.import_icon), // Your import icon drawable
                contentDescription = "Import Icon"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Import")
        }
    }
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
            if (columns.size >= 5) {
                val id = columns[0].toInt()
                val location = columns[1]
                val latitude = columns[2].toDouble()
                val longitude = columns[3].toDouble()
                val altitude = columns[4].toDouble()
                locationHistory.add(LocationItem(id, location, latitude, longitude, altitude))
            }
        }

        csvReader.close()
    }

    return locationHistory
}