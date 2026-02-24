package com.musicai.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.AudioRendererEventListener
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * PreviewPlayerViewModel:
 * - Plays Spotify preview URLs using Media3 ExoPlayer
 * - Captures PCM audio frames from the player in real-time
 * - Streams waveform & forwards PCM to AnalyzeViewModel
 */
class PreviewPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val player = ExoPlayer.Builder(application).build()

    /** Real-time "is playing" state for UI */
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    /** Live PCM waveform streamed from the player */
    val liveWaveform = MutableStateFlow(FloatArray(0))

    /** Optional analyzer target */
    var analyzeViewModel: AnalyzeViewModel? = null

    fun attachAnalyzer(vm: AnalyzeViewModel) {
        analyzeViewModel = vm
    }

    private var currentUrl: String? = null

    fun play(url: String) {
        if (url != currentUrl) {
            currentUrl = url
            player.setMediaItem(
                androidx.media3.common.MediaItem.fromUri(url)
            )
            player.prepare()
        }
        player.playWhenReady = true
        _isPlaying.value = true
    }

    fun pause() {
        player.playWhenReady = false
        _isPlaying.value = false
    }

    // -----------------------------------------------------------
    // REAL-TIME PCM CAPTURE FROM EXOPLAYER (Media3)
    // -----------------------------------------------------------
    @OptIn(UnstableApi::class)
    private val audioListener = object : AudioRendererEventListener {

        override fun onAudioInputFormatChanged(
            format: Format,
            unused: AudioSink.Configuration?
        ) {
            // no-op
        }

        override fun onAudioPositionAdvancing(playoutStartSystemTimeMs: Long) {
            // no-op
        }

        override fun onAudioDataEncoded(data: ByteArray, size: Int) {
            // no-op (rare case)
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
            // Convert byte PCM → short PCM
            val shorts = ShortArray(size / 2)
            ByteBuffer.wrap(buffer, offset, size)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
                .get(shorts)

            onPcmCaptured(shorts)
        }
    }

    private fun onPcmCaptured(pcm: ShortArray) {
        // For visual waveform:
        liveWaveform.value = pcm.map { it / 32768f }.toFloatArray()

        // For BPM/key analysis:
        analyzeViewModel?.analyze(pcm, 44100)
    }

    init {
        // Register listener ONCE
        player.addAudioDebugListener(audioListener)
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
