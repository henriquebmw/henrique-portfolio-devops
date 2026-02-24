package com.musicai.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicai.app.ai.AiEngine
import com.musicai.app.ui.chat.ChatUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val engine: AiEngine = AiEngine()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatUiMessage>>(emptyList())
    val messages: StateFlow<List<ChatUiMessage>> = _messages

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Add user message
        _messages.value = _messages.value + ChatUiMessage(text, true)

        viewModelScope.launch {
            try {
                val reply = engine.generateLyrics(  // reuse engine for general responses
                    theme = text,
                    mood = "neutral",
                    lines = 4
                )
                _messages.value = _messages.value + ChatUiMessage(reply, false)
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatUiMessage(
                    text = "Error: ${e.message}",
                    isUser = false
                )
            }
        }
    }

    fun clear() {
        _messages.value = emptyList()
    }
}
