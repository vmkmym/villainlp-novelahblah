package com.example.villainlp.chat.geminiChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiChatViewModel(
    generativeModel: GenerativeModel,
    private val model: GeminiChatModel
) : ViewModel() {

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "model") { text("이야기를 작성해보세요.") }
        )
    )

    private val _uiState: MutableStateFlow<GeminiChatUiState> =
        MutableStateFlow(GeminiChatUiState(chat.history.map { content ->
            // Map the initial messages
            GeminiChatMessage(
                message = content.parts.first().asTextOrNull() ?: "",
                userName = if (content.role == "user") GeminiChatParticipant.USER else GeminiChatParticipant.MODEL,
                uploadDate = ""
            )
        }))
    val uiState: StateFlow<GeminiChatUiState> =
        _uiState.asStateFlow()


    fun createChatRoom(title: String): String {
        return model.createChatRoom(title)
    }

    fun sendMessage(userMessage: String) {
        val geminiChatMessage = GeminiChatMessage(
            message = userMessage,
            userName = GeminiChatParticipant.USER,
            isPending = true,
            uploadDate = ""
        )
        _uiState.value.addMessage(geminiChatMessage)

        // Save user message to Firebase
        model.saveChatMessage(geminiChatMessage, "chatRoomId") // Replace "chatRoomId" with the actual chat room ID

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    val geminiChatbotMessage = GeminiChatMessage(
                        message = modelResponse,
                        userName = GeminiChatParticipant.MODEL,
                        isPending = false,
                        uploadDate = ""
                    )
                    _uiState.value.addMessage(geminiChatbotMessage)

                    // Save model response to Firebase
                    model.saveChatbotMessage(geminiChatbotMessage, "chatRoomId") // Replace "chatRoomId" with the actual chat room ID
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    GeminiChatMessage(
                        message = e.localizedMessage.toString(),
                        userName = GeminiChatParticipant.ERROR,
                        uploadDate = ""
                    )
                )
            }
        }
    }

    fun saveChatMessage(geminiChatMessage: GeminiChatMessage, title: String) {
        model.saveChatMessage(geminiChatMessage, title)
    }

    fun saveChatbotMessage(geminiChatbotMessage: GeminiChatMessage, title: String) {
        model.saveChatbotMessage(geminiChatbotMessage, title)
    }

    fun loadChatMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String) {
        model.loadChatMessages(listener, title)
    }

    fun loadChatbotMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String) {
        model.loadChatbotMessages(listener, title)
    }
}