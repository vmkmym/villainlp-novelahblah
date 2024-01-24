package com.example.villainlp.chat.openAichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.library.NovelInfo
import com.example.villainlp.server.FirebaseTools
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel(private val auth: FirebaseAuth) : ViewModel() {
    // 채팅 삭제 확인 대화 상자의 표시 여부를 관리하는 StateFlow
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // 현재 선택된 채팅의 문서 ID를 관리하는 StateFlow
    private val _documentID = MutableStateFlow("")
    val documentID: StateFlow<String> = _documentID.asStateFlow()

    // 현재 사용자의 소설 정보를 관리하는 StateFlow
    private val _novelInfo = MutableStateFlow<List<NovelInfo>>(emptyList())
    val novelInfo: StateFlow<List<NovelInfo>> = _novelInfo.asStateFlow()

    // ViewModel이 생성될 때 소설 정보를 가져온다.
    init {
        fetchNovelInfoDataFromFirestore()
    }

    // Firestore에서 소설 정보를 가져오는 함수
    private fun fetchNovelInfoDataFromFirestore() {
        viewModelScope.launch {
            _novelInfo.value = FirebaseTools.fetchNovelInfoDataFromFirestore(auth.currentUser?.uid ?: "")
        }
    }

    // Firestore에서 채팅을 삭제하는 함수
    fun deleteChatting() {
        FirebaseTools.deleteChattingFromFirestore(_documentID.value)
        _showDialog.value = false
    }

    // 채팅 삭제 확인 대화 상자를 표시하는 함수
    fun showDialog(documentID: String) {
        _documentID.value = documentID
        _showDialog.value = true
    }

    // 채팅 삭제 확인 대화 상자를 숨기는 함수
    fun hideDialog() {
        _showDialog.value = false
    }

    // Firestore에서 채팅 목록을 불러오는 함수
    fun fetchChatList() {
        viewModelScope.launch {
            _novelInfo.value = FirebaseTools.fetchNovelInfoDataFromFirestore(auth.currentUser?.uid ?: "")
        }
    }
}