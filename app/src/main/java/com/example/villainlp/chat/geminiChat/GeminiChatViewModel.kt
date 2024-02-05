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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            GeminiChatMessage(
                message = content.parts.first().asTextOrNull() ?: "",
                userName = if (content.role == "user") GeminiChatParticipant.USER else GeminiChatParticipant.MODEL,
                uploadDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            )
        }))
    val uiState: StateFlow<GeminiChatUiState> = _uiState.asStateFlow()


    private fun createChatRoom(title: String): String {
        return model.createChatRoom(title)
    }

    fun sendMessage(userMessage: String, title: String) {
        val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val geminiChatMessage = GeminiChatMessage(
            message = userMessage,
            userName = GeminiChatParticipant.USER,
            isPending = true,
            uploadDate = currentDate
        )
        _uiState.value.addMessage(geminiChatMessage)
        val chatRoomId = createChatRoom(title)
        saveChatMessage(geminiChatMessage, chatRoomId)

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    val geminiChatbotMessage = GeminiChatMessage(
                        message = modelResponse,
                        userName = GeminiChatParticipant.MODEL,
                        isPending = false,
                        uploadDate = currentDate
                    )
                    _uiState.value.addMessage(geminiChatbotMessage)
                    model.saveChatbotMessage(geminiChatbotMessage, chatRoomId)
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    GeminiChatMessage(
                        message = e.localizedMessage?.toString(),
                        userName = GeminiChatParticipant.ERROR,
                        uploadDate = currentDate
                    )
                )
                e.printStackTrace() // Log the full stack trace
            }
        }
    }

    private fun saveChatMessage(geminiChatMessage: GeminiChatMessage, title: String) {
        model.saveChatMessage(geminiChatMessage, title)
    }

    fun loadChatMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String) {
        model.loadChatMessages(listener, title)
    }

    fun loadChatbotMessages(listener: (List<GeminiChatMessage>) -> Unit, title: String) {
        model.loadChatbotMessages(listener, title)
    }
}