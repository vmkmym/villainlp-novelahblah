package com.villainlp.novlahvlah.chat.geminiChat

import androidx.compose.runtime.toMutableStateList

class GeminiChatUiState(
    messages: List<GeminiChatMessage> = emptyList()
) {
    private val _messages: MutableList<GeminiChatMessage> = messages.toMutableStateList()
    val messages: List<GeminiChatMessage> = _messages

    fun addMessage(msg: GeminiChatMessage) {
        _messages.add(msg)
    }

    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}