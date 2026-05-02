package com.shkurta.level

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shkurta.level.ui.level.LevelScreen
import com.shkurta.level.ui.theme.LevelTheme

class MainActivity : ComponentActivity() {
    private lateinit var levelSensor: LevelSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        levelSensor = LevelSensorManager(this)
        enableEdgeToEdge()
        setContent {
            LevelTheme {
                LevelScreen(sensorManager = levelSensor)
            }
        }
    }

    override fun onResume() { super.onResume(); levelSensor.start() }
    override fun onPause()  { super.onPause();  levelSensor.stop()  }
}