package com.techcravers.gpsbeacon

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.techcravers.gpsbeacon.BottomNavigationBar
import kotlin.math.sqrt

class AccelerometerActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var x = mutableStateOf(0f)
    private var y = mutableStateOf(0f)
    private var z = mutableStateOf(0f)

    private var lastTimestamp: Long = 0
    private var totalDistance: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            AccelerometerScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (lastTimestamp != 0L) {
                val deltaTime = (it.timestamp - lastTimestamp) * NS2S // Convert nanoseconds to seconds
                val displacement = calculateDisplacement(it.values[0], it.values[1], it.values[2], deltaTime)
                totalDistance += displacement
            }
            lastTimestamp = it.timestamp
            x.value = it.values[0]
            y.value = it.values[1]
            z.value = it.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something if sensor accuracy changes
    }

    private fun calculateDisplacement(ax: Float, ay: Float, az: Float, deltaTime: Float): Float {
        // Calculate magnitude of acceleration
        val acceleration = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()
        // Calculate displacement using the formula s = 0.5 * a * t^2
        return 0.5f * acceleration * deltaTime * deltaTime
    }

    @Composable
    fun AccelerometerScreen() {
        val context = LocalContext.current

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    AccelerometerContent()
                }
                BottomNavigationBar(context = context, selectedTab = 2)
            }
        }
    }

    @Composable
    fun AccelerometerContent() {
        var resetPressed by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Accelerometer Readings", modifier = Modifier.padding(bottom = 16.dp))
            Text(text = "X: ${x.value}")
            Text(text = "Y: ${y.value}")
            Text(text = "Z: ${z.value}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Total Distance: $totalDistance meters")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { resetPressed = true }) {
                Text("Reset Distance")
            }
        }

        if (resetPressed) {
            totalDistance = 0f
            resetPressed = false
        }
    }

    companion object {
        private const val NS2S = 1.0f / 1000000000.0f // Nanoseconds to seconds conversion factor
    }
}
