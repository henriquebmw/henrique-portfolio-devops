package com.musicai.app.ui.screens

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.musicai.app.R
import kotlin.math.sin

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit = {},
    onOpenAnalyze: () -> Unit = {},
    onOpenAi: () -> Unit = {},
    onOpenChat: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1DA1F2), Color(0xFF6C5CE7))
    )
}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        PulsingWaves()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(id = R.drawable.melodymind_logo),
                contentDescription = "MelodyMind",
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "MelodyMind",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )
            Text(
                text = "Create • Analyze • Inspire",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(8.dp))

            // TiltCards com ícones
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TiltCard(
                    title = "Search",
                    iconRes = R.drawable.ic_search_solid,
                    color = Color(0xFF65E0A3),
                    onClick = onOpenSearch
                )
                TiltCard(
                    title = "Analyze",
                    iconRes = R.drawable.ic_analyze_solid,
                    color = Color(0xFFEBC85E),
                    onClick = onOpenAnalyze
                )
                TiltCard(
                    title = "AI",
                    iconRes = R.drawable.ic_ai_solid,
                    color = Color(0xFFE06666),
                    onClick = onOpenAi
                )
            }
        }
    }
}

@Composable
private fun PulsingWaves() {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val waveT1 by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "t1"
    )
    val waveT2 by infinite.animateFloat(
        initialValue = 0.5f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(5000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "t2"
    )
    val pulseRadius by infinite.animateFloat(
        initialValue = 60f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "r"
    )
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.22f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Restart),
        label = "a"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        fun yAt(x: Float, t: Float, amp: Float, freq: Float, base: Float) =
            base + amp * sin((x / w) * freq + t * 6.283f)

        // Wave 1
        val base1 = h * 0.72f
        val amp1 = h * 0.06f
        val freq1 = 8f
        var x = 0f
        val step = 6f
        while (x < w) {
            val y0 = yAt(x, waveT1, amp1, freq1, base1)
            val y1 = yAt(x + step, waveT1, amp1, freq1, base1)
            drawLine(
                color = Color(0xFF1DA1F2).copy(alpha = 0.35f),
                start = Offset(x, y0), end = Offset(x + step, y1), strokeWidth = 8f
            )
            x += step
        }

        // Wave 2
        val base2 = h * 0.78f
        val amp2 = h * 0.08f
        val freq2 = 10f
        x = 0f
        while (x < w) {
            val y0 = yAt(x, waveT2, amp2, freq2, base2)
            val y1 = yAt(x + step, waveT2, amp2, freq2, base2)
            drawLine(
                color = Color(0xFF6C5CE7).copy(alpha = 0.30f),
                start = Offset(x, y0), end = Offset(x + step, y1), strokeWidth = 10f
            )
            x += step
        }

        // Pulse
        drawCircle(
            color = Color.White.copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = Offset(w / 2, h / 2)
        )
    }
}

@Composable
private fun TiltCard(
    title: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(SensorManager::class.java) }

    var rotX by remember { mutableStateOf(0f) }
    var rotY by remember { mutableStateOf(0f) }

    // Device tilt sensor
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    rotX = ((event.values.getOrNull(1) ?: 0f) * 14f).coerceIn(-14f, 14f)
                    rotY = ((event.values.getOrNull(0) ?: 0f) * -14f).coerceIn(-14f, 14f)
                }
            }
        }
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager?.unregisterListener(listener) }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.22f)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer {
                rotationX = rotX
                rotationY = rotY
                shadowElevation = 20f
                cameraDistance = 18 * density
            },
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
