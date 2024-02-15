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
    private val chatHistory = listOf(
        content("user") {
            text("나와 너는 릴레이로 소설을 이어서 작성할거야. 내가 먼저 시작하면 너가 이어서 작성해줘. 만약 소설을 이어서 작성하다가 내가 다른 소설을 작성하고 싶다는 의사를 너한테 밝히고 다른 이야기를 시작하면 그 이야기에 맞춰서 이어서 작성해줘.")
        },
        content("model") {
            text("알겠습니다. 이야기를 시작해주세요.")
        },
        content("user") {
            text("재능있는 남자 미대생 서우재, 재능있는 여자 미대생 구해원, 그리고 취미로 미술을 배우는 부잣집 아내 정희주를 중심으로 치정 멜로 이야기를 작성하고 싶어. 미대생 우재와 해원은 선후배 사이고 연인사이야. 미술 과외로 돈을 벌고 있어. 희주가 해원한테 과외를 받다가 어느 날 자신이 사정이 생겨 우재를 대신 보내. 여기서 부터 다음 이야기를 작성해줘. 결국은 희주가 우재와 불륜을 하지만 자신의 가정을 지키려고 하고 우재는 희주를 놓지 못하고 좋아하는 사람으로 비춰지는 스토리여야 해.")
        },
        content("model") {
            text("한 가족의 평범한 삶이 뜻밖의 사건으로 어지러지기 시작했다. 서우재와 구해원은 예술을 사랑하는 미대생 커플로, 그들은 서로의 재능을 존중하고 지지하며 행복한 연애를 이어가고 있었다. \n\n그런데 어느 날, 구해원의 미술 과외 수업에 참여하게 된 정희주가 이 가족의 운명을 바꾸게 되었다.희주는 첫 미술 수업 때부터 우재와의 인연에 이상한 감정을 느꼈다. \n\n그러나 처음에는 이 감정을 무시하려고 했고, 자신이 부주의하게 어긴 간접 키스 역시 그의 재능을 칭찬하는 명분으로 감추려고 했다. 그러나 우재는 희주의 마음을 읽어내고, 서로의 관계가 점차 얽히기 시작했다.")
        },
        content("user") {
            text("희주는 부부 간의 갈등 속에서 우재에게 편안함을 느끼게 되었다. 우재 또한 희주에게 서로 이해해주고 지지해주는 특별한 친구로서의 존재가 되어갔다.")
        },
        content("model") {
            text("그들의 관계는 어느 순간에는 친구를 넘어선 더 깊은 감정으로 번져갔다. 그러나 불륜은 항상 어두운 그림자를 들여놓는다. \n\n희주는 가정을 위협하는 이 새로운 감정에 대해 내심 갈등하며, 우재는 자신의 감정을 어떻게 희주에게 표현해야 할지 막막해졌다.")
        },
        content("user") {
            text("이제 이야기를 그만 작성하고 싶어.")
        },
        content("model") {
            text("알겠습니다. 다시 이야기를 이어가고 싶으시면 먼저 이야기를 시작해주세요.")
        }
    )

    val chat = generativeModel.startChat(
        history = chatHistory
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

    fun sendMessage(userMessage: String, title: String, userId: String) { // TODO : uid 추가
        val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val geminiChatMessage = GeminiChatMessage(
            message = userMessage,
            userId = userId,
            userName = GeminiChatParticipant.USER,
            isPending = true,
            uploadDate = currentDate
        )
        _uiState.value.addMessage(geminiChatMessage)
//        val chatRoomId = createChatRoom(title) // TODO : 이렇게 만들어내 chatRoomID는 랜덤임 계속 변하는값
        saveChatMessage(geminiChatMessage, title) // TODO : 그래서 채팅을 저장할 수 없음

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
                    model.saveChatbotMessage(geminiChatbotMessage, title)
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