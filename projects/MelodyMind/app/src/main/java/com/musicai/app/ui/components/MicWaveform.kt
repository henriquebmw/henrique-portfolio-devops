package com.musicai.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Displays a real-time microphone waveform.
 * @param waveData FloatArray containing normalized audio samples (-1.0 to 1.0)
 */
@Composable
fun MicWaveform(waveData: FloatArray) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        if (waveData.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val step = w / waveData.size
        val centerY = h / 2f
        val lineColor = Color(0xFF6C5CE7)

        var x = 0f
        for (i in waveData.indices) {
            val sample = waveData[i].coerceIn(-1f, 1f)
            val y = centerY - (sample * (centerY * 0.9f))
            drawLine(
                color = lineColor,
                start = Offset(x, centerY),
                end = Offset(x, y),
                strokeWidth = 2f
            )
            x += step
        }
    }
}
