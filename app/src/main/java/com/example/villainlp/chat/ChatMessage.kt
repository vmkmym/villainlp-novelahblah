@file:OptIn(BetaOpenAI::class)

package com.example.villainlp.chat

import com.aallam.openai.api.BetaOpenAI


data class TextContent(
    val subtitle: String,
    val body: String
)


data class ChatMessage(
    val message: String? = "message error",
    val userId: String? = "UID error",
    val userName: String? = "챗봇",
    val uploadDate: String? = ""
)

data class ChatbotMessage(
    val message: String? = null,
    val uploadDate: String? = ""
)

