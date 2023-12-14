@file:OptIn(BetaOpenAI::class)

package com.example.villainlp.model

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.thread.ThreadId

data class Message(
    val text: String,
    val isBot: Boolean
)

data class ChatMessage(
    val message: String? = "message error",
    val userId: String? = "UID error",
    val userName: String? = "Villain Bot",
    val uploadDate: String? = ""
)

// 이 부분에서 타입 에러 나서 앱이 튕김
data class ChatbotMessage(
    val message: Message? = null,
    val messageContent: List<MessageContent>? = null
)