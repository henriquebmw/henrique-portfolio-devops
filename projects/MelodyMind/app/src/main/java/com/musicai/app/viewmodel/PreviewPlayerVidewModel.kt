package com.musicai.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.AudioRendererEventListener
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    // -----------------------------------------------------------------------------------------
    //  PCM Capture Listener
    // -----------------------------------------------------------------------------------------
    @OptIn(UnstableApi::class)
    private val audioListener = object : AudioRendererEventListener {

        override fun onAudioInputFormatChanged(format: Format, unused: AudioSink.Configuration?) {
            // no-op
        }

        override fun onAudioPositionAdvancing(playoutStartSystemTimeMs: Long) {
            // no-op
        }

        override fun onAudioDataEncoded(data: ByteArray, size: Int) {
            // Rare case; ignore
        }

        override fun onAudioUnderrun(
            bufferSize: Int,
            bufferSizeMs: Long,
            elapsedSinceLastFeedMs: Long
        ) {
            // no-op
        }

        override fun onAudioBufferReleased(
            buffer: ByteArray,
            offset: Int,
            size: Int
        ) {
            // Convert bytes -> PCM samples
            val shortCount = size / 2
            val shorts = ShortArray(shortCount)

            ByteBuffer.wrap(buffer, offset, size)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .get(shorts)

            onPcmCaptured(shorts)
        }
    }

    private fun onPcmCaptured(pcm: ShortArray) {
        // Live waveform for UI (range -1.0..1.0)
        liveWaveform.value = pcm.map { it / 32768f }.toFloatArray()

        // Forward to audio analyzer if attached
        analyzeViewModel?.analyze(pcm, 44100)
    }

    init {
        // Register PCM listener ONCE
        player.addAudioDebugListener(audioListener)
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
