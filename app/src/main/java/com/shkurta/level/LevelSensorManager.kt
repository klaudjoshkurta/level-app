package com.shkurta.level

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LevelSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var tiltX by mutableStateOf(0f)
        private set
    var tiltY by mutableStateOf(0f)
        private set

    fun start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Normalize: divide by gravity constant to get range roughly -1..1
        tiltX = event.values[0] / SensorManager.GRAVITY_EARTH  // left/right
        tiltY = event.values[1] / SensorManager.GRAVITY_EARTH  // up/down
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}