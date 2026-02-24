package com.musicai.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musicai.app.viewmodel.AnalyzeViewModel
import com.musicai.app.ui.components.MicWaveform

@Composable
fun AnalyzeScreen(vm: AnalyzeViewModel = viewModel()) {

    val analyzeState by vm.state.collectAsState()
    val liveWave by vm.liveWaveform.collectAsState()

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) vm.startMicrophone()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            "Real‑Time Audio Analyzer",
            style = MaterialTheme.typography.headlineMedium
        )

        // ------------------------------
        // Microphone controls
        // ------------------------------

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }) {
                Text("Start Microphone")
            }

            OutlinedButton(onClick = vm::stopMicrophone) {
                Text("Stop")
            }
        }

        // ------------------------------
        // Live Waveform Viewer
        // ------------------------------

        Text("Live Waveform", style = MaterialTheme.typography.titleMedium)

        if (liveWave.isNotEmpty()) {
            MicWaveform(liveWave)
        } else {
            Text("Microphone not streaming yet.")
        }

        Divider()

        // ------------------------------
        // Final analyzed metrics
        // ------------------------------

        Text("Analysis Result", style = MaterialTheme.typography.titleMedium)

        Text("BPM: ${analyzeState.bpm ?: "?"}")
        Text("Key: ${analyzeState.key ?: "?"}")
        Text("Confidence: ${(analyzeState.confidence * 100).toInt()}%")

        analyzeState.error?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        // ------------------------------
        // Static waveform (from last full analysis)
        // ------------------------------

        if (analyzeState.waveform.isNotEmpty()) {
            Text("Captured Waveform", style = MaterialTheme.typography.titleMedium)
            CapturedWaveform(analyzeState.waveform)
        }
    }
}

@Composable
private fun CapturedWaveform(wave: FloatArray) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val w = size.width
        val h = size.height
        val step = w / wave.size

        var x = 0f
        for (i in wave.indices) {
            val y = (h / 2f) - (wave[i] * (h / 2f))
            drawLine(
                color = Color(0xFF6C5CE7),
                start = Offset(x, h / 2f),
                end = Offset(x, y),
                strokeWidth = 2f
            )
            x += step
        }
    }
}
