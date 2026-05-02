package com.shkurta.level.ui.level

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shkurta.level.LevelSensorManager
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

@Composable
fun LevelScreen(sensorManager: LevelSensorManager) {
    val tiltX = sensorManager.tiltX
    val tiltY = sensorManager.tiltY

    // Consider "level" if within ±2° tolerance
    val isLevel = abs(tiltX) < 0.035f && abs(tiltY) < 0.035f
    val bubbleColor = if (isLevel) Color(0xFF00C853) else Color(0xFFFF1744)

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            val radius = size.minDimension / 2f
            val bubbleRadius = radius * 0.12f

            // Outer circle
            drawCircle(color = Color.White.copy(alpha = 0.2f), style = Stroke(width = 2.dp.toPx()))

            // Center crosshair
            drawLine(Color.White.copy(alpha = 0.3f),
                start = Offset(center.x - radius * 0.3f, center.y),
                end = Offset(center.x + radius * 0.3f, center.y))
            drawLine(Color.White.copy(alpha = 0.3f),
                start = Offset(center.x, center.y - radius * 0.3f),
                end = Offset(center.x, center.y + radius * 0.3f))

            // Bubble position — clamp so it stays inside the circle
            val rawX = -tiltX * radius * 0.8f
            val rawY = tiltY * radius * 0.8f
            val dist = sqrt(rawX * rawX + rawY * rawY)
            val maxDist = radius - bubbleRadius
            val scale = if (dist > maxDist) maxDist / dist else 1f
            val bubbleX = center.x + rawX * scale
            val bubbleY = center.y + rawY * scale

            // Bubble
            drawCircle(color = bubbleColor.copy(alpha = 0.3f),
                radius = bubbleRadius * 1.5f, center = Offset(bubbleX, bubbleY))
            drawCircle(color = bubbleColor,
                radius = bubbleRadius, center = Offset(bubbleX, bubbleY))
        }

        // Angle readout
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 340.dp)) {
            Text("X: ${"%.1f".format(Math.toDegrees(asin(tiltX.toDouble())))}°",
                color = Color.White)
            Text("Y: ${"%.1f".format(Math.toDegrees(asin(tiltY.toDouble())))}°",
                color = Color.White)
            if (isLevel) Text("LEVEL ✓", color = Color(0xFF00C853),
                fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}