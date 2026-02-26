package com.musicai.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicai.app.audio.AudioAnalyzer
import com.musicai.app.audio.MicRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel that drives the real‑time audio analyzer UI.
 *
 * Responsibilities:
 *  - start/stop microphone capture
 *  - forward PCM to AudioAnalyzer and publish results
 *  - expose live waveform and analysis state as flows
 */
class AnalyzeViewModel : ViewModel() {

    data class State(
        val bpm: Int? = null,
        val key: String? = null,
        val confidence: Float = 0f,
        val waveform: FloatArray = FloatArray(0),
        val error: String? = null
    )

    private val analyzer = AudioAnalyzer()

    /** convenient helper wrapping the analyzer transpose method */
    fun transposeKey(chord: String, semitones: Int): String =
        analyzer.transposeKey(chord, semitones)
    private val recorder = MicRecorder()

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _liveWaveform = MutableStateFlow(FloatArray(0))
    val liveWaveform: StateFlow<FloatArray> = _liveWaveform

    fun startMicrophone() {
        recorder.start { pcm ->
            // update live waveform quickly
            _liveWaveform.value = pcm.map { it / 32768f }.toFloatArray()

            // run analyzer on background thread
            viewModelScope.launch {
                try {
                    val result = analyzer.analyze(pcm, 44100)
                    _state.value = _state.value.copy(
                        bpm = result.bpm,
                        key = result.key,
                        confidence = result.confidence,
                        waveform = result.waveform,
                        error = null
                    )
                } catch (e: Exception) {
                    _state.value = _state.value.copy(error = e.message ?: "unknown")
                }
            }
        }
    }

    fun stopMicrophone() {
        recorder.stop()
    }
}
