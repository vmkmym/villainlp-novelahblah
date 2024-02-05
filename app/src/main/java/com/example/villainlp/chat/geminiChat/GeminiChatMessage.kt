package com.example.villainlp.chat.geminiChat

import java.util.UUID

// TODO: userId를 어떻게 선언해야될지모르겠음

data class GeminiChatMessage(
    var message: String? = "message error",
//    val userId: String? = UUID.randomUUID().toString(),
    val userId: String? = "UID error",
    val userName: GeminiChatParticipant? = GeminiChatParticipant.USER,
    var isPending: Boolean = false, // true: user message, false: model response
    val uploadDate: String? = ""
)
