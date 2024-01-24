package com.example.villainlp.chat.openAichat

import androidx.lifecycle.ViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.thread.ThreadId

@OptIn(BetaOpenAI::class)
class ChatViewModel(private val model: ChatModel): ViewModel() {
    fun saveChatMessage(chatMessage: ChatMessage, title: String, threadId: ThreadId?) {
        model.saveChatMessage(chatMessage, title, threadId)
    }

    fun saveChatbotMessage(chatbotMessage: ChatbotMessage, title: String, threadId: ThreadId?) {
        model.saveChatbotMessage(chatbotMessage, title, threadId)
    }

    fun loadChatMessages(listener: (List<ChatMessage>) -> Unit, title: String, threadId: ThreadId?) {
        model.loadChatMessages(listener, title, threadId)
    }

    fun loadChatbotMessages(listener: (List<ChatbotMessage>) -> Unit, title: String, threadId: ThreadId?) {
        model.loadChatBotMessages(listener, title, threadId)
    }

}