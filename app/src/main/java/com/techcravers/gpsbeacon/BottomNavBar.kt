package com.techcravers.gpsbeacon

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(context: Context, selectedTab: Int) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
            label = { Text("Location") },
            selected = selectedTab == 1,
            onClick = {
                val intent = Intent(context, LocationActivity::class.java)
                context.startActivity(intent)
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Face, contentDescription = "Accelerometer") },
            label = { Text("Accelerometer") },
            selected = selectedTab == 2,
            onClick = {
                val intent = Intent(context, AccelerometerActivity::class.java)
                context.startActivity(intent)
            }
        )
    }
}
