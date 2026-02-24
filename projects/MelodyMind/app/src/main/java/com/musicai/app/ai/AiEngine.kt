package com.musicai.app.ai

import com.musicai.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

/**
 * AiEngine
 * - Online: calls OpenAI (gpt-4o-mini) via Chat Completions
 * - Offline: simple heuristics for chords & lyrics if API key is missing
 */
class AiEngine(
    private val apiKey: String = BuildConfig.OPENAI_API_KEY
) {
    private val model = "gpt-4o-mini"   // chosen for speed/cost balance

    private val service: OpenAIService by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val http = OkHttpClient.Builder()
            .addInterceptor(log)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(http)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }

    // ----- PUBLIC API -----

    suspend fun generateLyrics(theme: String, mood: String, lines: Int = 8): String =
        if (apiKey.isNullOrBlank()) offlineLyrics(theme, mood, lines)
        else onlineLyrics(theme, mood, lines)

    suspend fun suggestChords(key: String, mode: String, style: String, bars: Int = 4): List<String> =
        if (apiKey.isNullOrBlank()) offlineChords(key, mode, bars)
        else onlineChords(key, mode, style, bars)

    // ----- ONLINE -----

    private suspend fun onlineLyrics(theme: String, mood: String, lines: Int): String = withContext(Dispatchers.IO) {
        val sys = ChatMessage(
            role = "system",
            content = "You are a concise lyricist. Write singable, non-copyrighted lines that fit modern pop structures."
        )
        val user = ChatMessage(
            role = "user",
            content = """
                Write about: $theme
                Mood: $mood
                Format: ${lines} short lines, no numbering, no quotes.
            """.trimIndent()
        )
        val res = service.chat(
            bearer = "Bearer $apiKey",
            req = ChatCompletionRequest(
                model = model,
                messages = listOf(sys, user),
                temperature = 0.85,
                maxTokens = 300
            )
        )
        res.choices.firstOrNull()?.message?.content?.trim().orEmpty().ifBlank {
            offlineLyrics(theme, mood, lines)
        }
    }

    private suspend fun onlineChords(key: String, mode: String, style: String, bars: Int): List<String> = withContext(Dispatchers.IO) {
        val sys = ChatMessage(
            role = "system",
            content = "You are a concise music theory assistant. Output ONLY a bar-by-bar chord list, separated by spaces. Avoid explanations."
        )
        val user = ChatMessage(
            role = "user",
            content = """
                Key: $key ${mode.lowercase()}
                Style: $style
                Bars: $bars
                Rules: Use common diatonic triads/sevenths that fit the style.
                Output format example (4 bars): C G Am F
            """.trimIndent()
        )
        val res = service.chat(
            bearer = "Bearer $apiKey",
            req = ChatCompletionRequest(
                model = model,
                messages = listOf(sys, user),
                temperature = 0.7,
                maxTokens = 120
            )
        )
        val line = res.choices.firstOrNull()?.message?.content?.trim().orEmpty()
        // Parse "C G Am F" etc.
        line.split(Regex("\\s+")).filter { it.isNotBlank() }.take(bars).ifEmpty {
            offlineChords(key, mode, bars)
        }
    }

    // ----- OFFLINE FALLBACKS -----

    private fun offlineLyrics(theme: String, mood: String, lines: Int): String {
        val openings = listOf(
            "Under neon skies, I chase the time,",
            "In the midnight hum, you cross my mind,",
            "Echoes on the floor, a fading sign,"
        )
        val mid = listOf(
            "we drift like waves across the night,",
            "a spark that glows then leaves a light,",
            "the world turns slow then feels just right,"
        )
        val outro = listOf(
            "and every chorus finds a rhyme.",
            "your name’s the rhythm in my spine.",
            "I hear your color in this line."
        )
        val rnd = Random(theme.hashCode() xor mood.hashCode())
        val pool = openings + mid + outro
        return (0 until lines).joinToString("\n") { pool.random(rnd) + " ($theme, $mood)" }
    }

    private fun offlineChords(key: String, mode: String, bars: Int): List<String> {
        val major = listOf(listOf("I","V","vi","IV"), listOf("I","vi","IV","V"), listOf("ii","V","I","vi"))
        val minor = listOf(listOf("i","VI","III","VII"), listOf("i","iv","VII","III"), listOf("ii°","v","i","VI"))
        val romans = if (mode.equals("minor", true)) minor.random() else major.random()
        val triads = romanToChords(key, mode, romans)
        return (0 until bars).map { triads[it % triads.size] }
    }

    // ----- helpers -----

    private fun romanToChords(key: String, mode: String, romans: List<String>): List<String> {
        val semis = listOf("C","C#","D","D#","E","F","F#","G","G#","A","A#","B")
        fun idx(n: String) = semis.indexOfFirst { it.equals(n, true) }.takeIf { it >= 0 } ?: 0
        val tonic = idx(key)
        fun degree(d: Int) = semis[(tonic + d + 12) % 12]

        val mapMaj = mapOf(
            "I" to degree(0), "ii" to degree(2)+"m", "iii" to degree(4)+"m",
            "IV" to degree(5), "V" to degree(7), "vi" to degree(9)+"m", "vii°" to degree(11)+"°"
        )
        val mapMin = mapOf(
            "i" to degree(0)+"m", "ii°" to degree(2)+"°", "III" to degree(3),
            "iv" to degree(5)+"m", "v" to degree(7)+"m", "VI" to degree(8), "VII" to degree(10)
        )
        val dict = if (mode.equals("minor", true)) mapMin else mapMaj
        return romans.mapNotNull { dict[it] }
    }
}
