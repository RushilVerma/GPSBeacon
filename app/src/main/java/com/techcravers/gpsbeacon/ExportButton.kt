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
import java.io.FileWriter

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