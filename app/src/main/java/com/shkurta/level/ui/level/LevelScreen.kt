package com.shkurta.level.ui.level

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.shkurta.level.LevelSensorManager
import com.shkurta.level.ui.theme.BackgroundCenter
import com.shkurta.level.ui.theme.BackgroundDeep
import com.shkurta.level.ui.theme.BubbleGlowA
import com.shkurta.level.ui.theme.BubbleGlowB
import com.shkurta.level.ui.theme.BubbleGlowC
import com.shkurta.level.ui.theme.CardBorder
import com.shkurta.level.ui.theme.CardSurface
import com.shkurta.level.ui.theme.CrosshairStroke
import com.shkurta.level.ui.theme.LevelGreen
import com.shkurta.level.ui.theme.RingStroke
import com.shkurta.level.ui.theme.TealAccent
import com.shkurta.level.ui.theme.TickStroke
import com.shkurta.level.ui.theme.TiltRed
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun LevelScreen(sensorManager: LevelSensorManager) {
    val tiltX = sensorManager.tiltX
    val tiltY = sensorManager.tiltY

    val isLevel = abs(tiltX) < 0.035f && abs(tiltY) < 0.035f

    val animBubbleX by animateFloatAsState(
        targetValue = -tiltX,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "bubbleX"
    )
    val animBubbleY by animateFloatAsState(
        targetValue = tiltY,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "bubbleY"
    )
    val bubbleColor by animateColorAsState(
        targetValue = if (isLevel) LevelGreen else TiltRed,
        animationSpec = tween(300),
        label = "bubbleColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(BackgroundCenter, BackgroundDeep),
                    radius = 1200f
                )
            )
    ) {
        // Header
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LEVEL",
                style = MaterialTheme.typography.titleLarge,
                color = TealAccent
            )
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(TealAccent.copy(alpha = 0.4f))
            )
        }

        // Level status pill
        AnimatedVisibility(
            visible = isLevel,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp),
            enter = fadeIn(tween(400)) + scaleIn(
                initialScale = 0.75f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
            ),
            exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.85f)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = LevelGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, LevelGreen.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "LEVEL",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = LevelGreen
                )
            }
        }

        // Instrument canvas
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
        ) {
            val radius = size.minDimension / 2f
            drawInstrumentFace(radius, bubbleColor, animBubbleX, animBubbleY)
        }

        // Angle readout card
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .fillMaxWidth(0.75f),
            shape = RoundedCornerShape(16.dp),
            color = CardSurface,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AngleReadout(
                    label = "X",
                    degrees = Math.toDegrees(asin(tiltX.toDouble().coerceIn(-1.0, 1.0)))
                )
                Box(
                    Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(CardBorder)
                )
                AngleReadout(
                    label = "Y",
                    degrees = Math.toDegrees(asin(tiltY.toDouble().coerceIn(-1.0, 1.0)))
                )
            }
        }
    }
}

@Composable
private fun AngleReadout(label: String, degrees: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = TealAccent.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${"%.1f".format(degrees)}°",
            style = MaterialTheme.typography.displaySmall,
            color = Color.White
        )
    }
}

private fun DrawScope.drawInstrumentFace(
    radius: Float,
    bubbleColor: Color,
    animBubbleX: Float,
    animBubbleY: Float
) {
    val bubbleRadius = radius * 0.12f

    // Outer boundary ring
    drawCircle(
        color = RingStroke,
        radius = radius,
        style = Stroke(width = 1.5.dp.toPx())
    )
    // 50% radius ring
    drawCircle(
        color = RingStroke.copy(alpha = 0.12f),
        radius = radius * 0.50f,
        style = Stroke(width = 1.dp.toPx())
    )
    // Level-zone ring (±2° target area) in teal
    drawCircle(
        color = TealAccent.copy(alpha = 0.25f),
        radius = radius * 0.20f,
        style = Stroke(width = 1.5.dp.toPx())
    )
    // Center dot
    drawCircle(color = CrosshairStroke, radius = 3.dp.toPx())

    // Crosshair (only inside the level-zone ring)
    val crossLen = radius * 0.18f
    drawLine(
        color = CrosshairStroke,
        start = Offset(center.x - crossLen, center.y),
        end = Offset(center.x + crossLen, center.y),
        strokeWidth = 1.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = CrosshairStroke,
        start = Offset(center.x, center.y - crossLen),
        end = Offset(center.x, center.y + crossLen),
        strokeWidth = 1.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Tick marks (36 × 10°, 4 major at cardinal points)
    val tickCount = 36
    val majorEvery = 9
    for (i in 0 until tickCount) {
        val angle = i * 360.0 / tickCount * (PI / 180.0)
        val isMajor = i % majorEvery == 0
        val tickLen = if (isMajor) radius * 0.08f else radius * 0.04f
        val alpha = if (isMajor) 0.50f else 0.25f
        val outerX = center.x + (radius * cos(angle)).toFloat()
        val outerY = center.y + (radius * sin(angle)).toFloat()
        val innerX = center.x + ((radius - tickLen) * cos(angle)).toFloat()
        val innerY = center.y + ((radius - tickLen) * sin(angle)).toFloat()
        drawLine(
            color = TickStroke.copy(alpha = alpha),
            start = Offset(innerX, innerY),
            end = Offset(outerX, outerY),
            strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Bubble position
    val rawX = animBubbleX * radius * 0.8f
    val rawY = animBubbleY * radius * 0.8f
    val dist = sqrt(rawX * rawX + rawY * rawY)
    val maxDist = radius - bubbleRadius
    val scale = if (dist > maxDist) maxDist / dist else 1f
    val bx = center.x + rawX * scale
    val by = center.y + rawY * scale
    val bubbleCenter = Offset(bx, by)

    // Glow layers (outermost to innermost)
    drawCircle(color = BubbleGlowA, radius = bubbleRadius * 3.0f, center = bubbleCenter)
    drawCircle(color = BubbleGlowB, radius = bubbleRadius * 2.2f, center = bubbleCenter)
    drawCircle(color = BubbleGlowC, radius = bubbleRadius * 1.6f, center = bubbleCenter)

    // Solid bubble fill
    drawCircle(color = bubbleColor, radius = bubbleRadius, center = bubbleCenter)

    // Specular highlight (top-left white dot for 3D feel)
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = bubbleRadius * 0.30f,
        center = Offset(bx - bubbleRadius * 0.28f, by - bubbleRadius * 0.28f)
    )
}
