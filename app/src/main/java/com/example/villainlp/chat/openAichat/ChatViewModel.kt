package com.example.villainlp.chat.openAichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<List<ChatMessage>> = MutableStateFlow(emptyList())
    val uiState: StateFlow<List<ChatMessage>> = _uiState.asStateFlow()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            val newMessage = ChatMessage(
                message = userMessage,
                userId = "user_id",  // TODO: Replace with actual user id
                userName = "user_name",  // TODO: Replace with actual user name
                uploadDate = "upload_date"  // TODO: Replace with actual upload date
            )
            _uiState.value = _uiState.value + newMessage

            // TODO: Implement sending the message to the server and receiving a response
            // For now, we simulate a bot response
            val botResponse = ChatMessage(
                message = "Bot response to: $userMessage",
                userId = "bot_id",
                userName = "bot_name",
                uploadDate = "upload_date"  // TODO: Replace with actual upload date
            )
            _uiState.value = _uiState.value + botResponse
        }
    }
}