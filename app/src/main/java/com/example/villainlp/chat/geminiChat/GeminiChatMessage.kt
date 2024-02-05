package com.example.villainlp.chat.geminiChat

import java.util.UUID

data class GeminiChatMessage(
    var message: String? = "message error",
    val userId: String? = UUID.randomUUID().toString(),
    val userName: GeminiChatParticipant? = GeminiChatParticipant.USER,
    var isPending: Boolean = false, // true: user message, false: model response
    val uploadDate: String? = ""
)
