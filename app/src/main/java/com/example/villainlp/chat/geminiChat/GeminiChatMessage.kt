package com.example.villainlp.chat.geminiChat

data class GeminiChatMessage(
    var message: String? = "message error",
    val userId: String? = "UID error",
    val userName: GeminiChatParticipant? = GeminiChatParticipant.USER,
    var isPending: Boolean = false, // true: user message, false: model response
    val uploadDate: String? = ""
)
