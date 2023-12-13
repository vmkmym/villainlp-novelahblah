package com.example.villainlp.model

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

data class ChatbotMessage(
    val message: String? = "",
)