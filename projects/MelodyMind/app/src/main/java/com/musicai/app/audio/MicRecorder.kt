package com.musicai.app.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

class MicRecorder(
    private val sampleRate: Int = 44100,
    private val bufferSize: Int = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )
) {
    private var recorder: AudioRecord? = null
    @Volatile private var isRecording = false

    fun start(onPcmData: (ShortArray) -> Unit) {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        recorder?.startRecording()
        isRecording = true

        Thread {
            val buf = ShortArray(bufferSize)
            while (isRecording) {
                val read = recorder?.read(buf, 0, buf.size) ?: 0
                if (read > 0) {
                    val pcm = buf.copyOf(read)
                    onPcmData(pcm)
                }
            }
        }.start()
    }

    fun stop() {
        isRecording = false
        recorder?.stop()
        recorder?.release()
        recorder = null
    }
}
