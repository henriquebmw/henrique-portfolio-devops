package com.musicai.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicai.app.ai.AiEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AiAssistantState(
    val theme: String = "",
    val mood: String = "happy",
    val key: String = "C",
    val mode: String = "major",
    val style: String = "pop",
    val lyrics: String = "",
    val chords: List<String> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class AiAssistantViewModel(
    private val engine: AiEngine = AiEngine()
) : ViewModel() {

    private val _state = MutableStateFlow(AiAssistantState())
    val state: StateFlow<AiAssistantState> = _state

    fun updateTheme(t: String) { _state.value = _state.value.copy(theme = t) }
    fun updateMood(m: String) { _state.value = _state.value.copy(mood = m) }
    fun updateKey(k: String) { _state.value = _state.value.copy(key = k) }
    fun updateMode(m: String) { _state.value = _state.value.copy(mode = m) }
    fun updateStyle(s: String) { _state.value = _state.value.copy(style = s) }

    fun generateAll() {
        val s = _state.value
        if (s.theme.isBlank()) {
            _state.value = s.copy(error = "Please set a theme first.")
            return
        }
        viewModelScope.launch {
            try {
                _state.value = s.copy(loading = true, error = null)

                val (lyrics, chords) = kotlinx.coroutines.async {
                    engine.generateLyrics(s.theme, s.mood, lines = 8)
                } to kotlinx.coroutines.async {
                    engine.suggestChords(s.key, s.mode, s.style, bars = 4)
                }

                _state.value = _state.value.copy(
                    lyrics = lyrics.await(),
                    chords = chords.await(),
                    loading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "Unknown error")
            }
        }
    }

    fun clearOutputs() {
        _state.value = _state.value.copy(lyrics = "", chords = emptyList(), error = null)
    }
}
