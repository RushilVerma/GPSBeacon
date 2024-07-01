package com.techcravers.gpsbeacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class HomePageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomePageContent()
            }
        }
    }
}

@Composable
fun HomePageContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Home Page", modifier = Modifier.padding(bottom = 16.dp))
        // Add your home page UI components here
    }
}