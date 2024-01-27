package com.example.villainlp.chat.geminiChat

import java.util.UUID

data class GeminiChatMessage(
    var message: String = "",
    val userId: String = UUID.randomUUID().toString(),
    val userName: GeminiChatParticipant = GeminiChatParticipant.USER,
    var isPending: Boolean = false,
    val uploadDate: String? = ""
)
