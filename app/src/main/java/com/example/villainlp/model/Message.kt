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

data class Message(
    val text: String,
    val isBot: Boolean
)

data class CreativeYard(
    val title: String,
    val type: String
)


data class ChatMessage(
    val message: String? = "message error",
    val userId: String? = "UID error",
    val userName: String? = "userName error",
    val uploadDate: String? = ""
)

data class ChatbotMessage(
    val message: String? = null,
)

// 이거 그냥 상은이가 참고하려고 만든 데이터 클래스,,, 추후 지울거임
@Serializable
@BetaOpenAI
public final data class PublicMessageBot(
    val id: MessageId,
    val createdAt: Int,
    val threadId: ThreadId,
    val role: Role,
    val content: List<MessageContent>,
    val assistantId: AssistantId?,
    val runId: RunId?,
    val fileIds: List<FileId>,
    val metadata: Map<String, String>
)