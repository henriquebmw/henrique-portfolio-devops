package com.musicai.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.musicai.app.viewmodel.AnalyzeViewModel
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * PreviewPlayerViewModel:
 * - Plays preview URLs using Media3 ExoPlayer
 * - Captures PCM frames from the player (real-time)
 * - Produces live waveform
 * - Can forward PCM to AnalyzeViewModel for BPM/Key analysis
 */
class PreviewPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val player = ExoPlayer.Builder(application).build()

    /** Real-time waveform streamed as FloatArray for UI */
    val liveWaveform = MutableStateFlow(FloatArray(0))

    /** Optional analyzer downstream (AnalyzeViewModel) */
    var analyzeViewModel: AnalyzeViewModel? = null

    fun attachAnalyzer(vm: AnalyzeViewModel) {
        analyzeViewModel = vm
    }

    /** Player state */
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var currentUrl: String? = null

    /** Play a preview URL with automatic prepare */
    fun play(url: String) {
        if (url != currentUrl) {
            currentUrl = url
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
        }
        player.playWhenReady = true
        _isPlaying.value = true
    }

    fun pause() {
        player.playWhenReady = false
        _isPlaying.value = false
    }

    // PCM capture toolbar removed – this functionality relied on a
    // version-specific Media3 listener interface that caused compilation
    // problems.  For now the player still works but no live audio frames are
    // reported.  If you later need to re‑enable capturing you can re‑introduce
    // the listener and update the method signatures to match the
    // `AudioRendererEventListener` provided by your Media3 dependency.

    // private fun onPcmCaptured(pcm: ShortArray) { ... }  // already defined below


    init {
        // no-op initialization; audio debugging listener disabled
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
