package com.musicai.app.ai

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Header

// -------------- API TYPES (chat.completions-compatible) --------------

data class ChatMessage(
    val role: String,           // "system" | "user" | "assistant"
    val content: String
)

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.9,
    @SerializedName("max_tokens") val maxTokens: Int = 512
)

data class ChatCompletionResponse(
    val id: String?,
    val choices: List<ChatChoice> = emptyList()
)

data class ChatChoice(
    val index: Int,
    val message: ChatMessage?
)

interface OpenAIService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun chat(
        @Header("Authorization") bearer: String,
        @Body req: ChatCompletionRequest
    ): ChatCompletionResponse
}
