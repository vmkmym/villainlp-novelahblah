@file:OptIn(BetaOpenAI::class)

package com.example.villainlp.model

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.thread.ThreadId
import kotlinx.serialization.Serializable


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

