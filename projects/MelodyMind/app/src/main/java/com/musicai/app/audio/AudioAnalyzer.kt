package com.musicai.app.audio

import kotlin.math.*
import kotlin.random.Random

/**
 * AudioAnalyzer
 * - Extracts BPM via energy-envelope autocorrelation.
 * - Extracts musical key via pitch-class histogram + template matching.
 * - Works entirely on-device without any third-party dependencies.
 *
 * Intended for short 5–20 sec preview audio.
 */
class AudioAnalyzer {

    data class AnalysisResult(
        val bpm: Int?,
        val key: String?,
        val confidence: Float,
        val waveform: FloatArray
    )

    /**
     * Main entry point for processing.
     * @param pcm 16-bit PCM mono
     * @param sampleRate 44100 or 48000 recommended
     */
    fun analyze(pcm: ShortArray, sampleRate: Int): AnalysisResult {
        val floats = pcm.map { it / 32768f }.toFloatArray()
        val bpm = estimateBpm(floats, sampleRate)
        val (key, conf) = estimateKey(floats, sampleRate)
        val wave = normalizeWaveform(floats)

        return AnalysisResult(
            bpm = bpm,
            key = key,
            confidence = conf,
            waveform = wave
        )
    }

    // -------------------------------------------------------------------------
    // BPM ESTIMATION (Envelope autocorrelation)
    // -------------------------------------------------------------------------

    private fun estimateBpm(x: FloatArray, fs: Int): Int? {
        // Use RMS envelope downsampled ~200 Hz for speed
        val frame = 1024
        val hop = 512
        val env = mutableListOf<Float>()
        var i = 0
        while (i + frame < x.size) {
            var sum = 0f
            for (k in 0 until frame) sum += x[i + k] * x[i + k]
            env += sqrt(sum / frame)
            i += hop
        }
        if (env.size < 32) return null

        val envArr = env.toFloatArray()
        val envRate = fs / hop.toFloat()

        // Search between 60–180 BPM
        val minBpm = 60
        val maxBpm = 180
        val minLag = (envRate * 60f / maxBpm).roundToInt().coerceAtLeast(2)
        val maxLag = (envRate * 60f / minBpm).roundToInt().coerceAtMost(envArr.size - 2)

        var bestLag = -1
        var bestCorr = 0f

        for (lag in minLag..maxLag) {
            var sum = 0f
            var n = 0
            for (t in 0 until envArr.size - lag) {
                sum += envArr[t] * envArr[t + lag]
                n++
            }
            val corr = if (n > 0) sum / n else 0f
            if (corr > bestCorr) {
                bestCorr = corr
                bestLag = lag
            }
        }

        if (bestLag <= 0) return null

        var bpm = (60f * envRate / bestLag).roundToInt()

        // Normalize half/double tempo
        while (bpm < 70) bpm *= 2
        while (bpm > 180) bpm /= 2
        return bpm
    }

    // -------------------------------------------------------------------------
    // KEY DETECTION (Pitch class profile)
    // -------------------------------------------------------------------------

    private fun estimateKey(x: FloatArray, fs: Int): Pair<String?, Float> {
        val hop = 2048
        val win = 4096

        val pcp = FloatArray(12)

        var i = 0
        while (i + win < x.size) {
            val f0 = estimateF0(x, i, win, fs)
            if (f0 > 50f) {
                val pc = hzToPitchClass(f0)
                pcp[pc] += 1f
            }
            i += hop
        }

        val majorT = floatArrayOf(
            6.35f, 2.23f, 3.48f, 2.33f, 4.38f, 4.09f,
            2.52f, 5.19f, 2.39f, 3.66f, 2.29f, 2.88f
        )

        val minorT = floatArrayOf(
            6.33f, 2.68f, 3.52f, 5.38f, 2.60f, 3.53f,
            2.54f, 4.75f, 3.98f, 2.69f, 3.34f, 3.17f
        )

        val keys = arrayOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B")

        fun rotate(a: FloatArray, shift: Int): FloatArray {
            val out = FloatArray(12)
            for (k in 0 until 12) out[k] = a[(k + shift) % 12]
            return out
        }

        fun dot(a: FloatArray, b: FloatArray): Float {
            var s = 0f
            for (i in 0 until 12) s += a[i] * b[i]
            return s
        }

        var bestScore = -1f
        var bestKey: String? = null
        var bestMode = "major"

        for (k in 0 until 12) {
            val maj = dot(pcp, rotate(majorT, k))
            if (maj > bestScore) {
                bestScore = maj
                bestKey = keys[k]
                bestMode = "major"
            }
            val min = dot(pcp, rotate(minorT, k))
            if (min > bestScore) {
                bestScore = min
                bestKey = keys[k]
                bestMode = "minor"
            }
        }

        val total = pcp.sum().coerceAtLeast(1f)
        val confidence = (bestScore / total).coerceIn(0f, 1f)

        return if (bestKey == null) null to 0f
        else {
            val name = if (bestMode == "major") bestKey else "${bestKey}m"
            name to confidence
        }
    }

    private fun hzToPitchClass(hz: Float): Int {
        val a4 = 440f
        val n = 12 * log2(hz / a4) + 69
        return ((n.roundToInt() % 12) + 12) % 12
    }

    /**
     * Simple chord/key transposition helper.  Accepts names like "C"/"Am"/"F#".
     * Returns the same chord shifted by the specified number of semitones.
     * Uses only chromatic semitone stepping, no accidentals conversion.
     */
    fun transposeKey(original: String, semitones: Int): String {
        val semis = listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B")
        if (original.isBlank()) return original
        // strip potential minor indicator
        val base = original.trimEnd('m','M','\#','b')
        val idx = semis.indexOfFirst { it.equals(base, true) }
        if (idx < 0) return original
        val newIdx = (idx + semitones + semis.size) % semis.size
        var result = semis[newIdx]
        if (original.endsWith("m", ignoreCase = true)) result += "m"
        return result
    }

    /**
     * YIN-like fundamental frequency estimation (simplified)
     */
    private fun estimateF0(x: FloatArray, start: Int, win: Int, fs: Int): Float {
        val minF = 55f
        val maxF = 1000f
        val minLag = (fs / maxF).toInt()
        val maxLag = (fs / minF).toInt().coerceAtMost(win - 2)

        var bestLag = -1
        var best = Float.MAX_VALUE

        for (lag in minLag..maxLag) {
            var d = 0f
            var n = 0
            for (i in 0 until win - lag) {
                val diff = x[start + i] - x[start + i + lag]
                d += diff * diff
                n++
            }
            if (n > 0) {
                val score = d / n
                if (score < best) {
                    best = score
                    bestLag = lag
                }
            }
        }
        return if (bestLag > 0) fs.toFloat() / bestLag else -1f
    }

    /**
     * Normalize waveform to [-1..1]
     */
    private fun normalizeWaveform(x: FloatArray): FloatArray {
        val maxAbs = x.maxOfOrNull { abs(it) } ?: 1f
        return FloatArray(x.size) { x[it] / maxAbs }
    }
}
