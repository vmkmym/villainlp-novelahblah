package com.villainlp.novlahvlah.chat.openAichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villainlp.novlahvlah.server.FirebaseTools
import com.villainlp.novlahvlah.shared.NovelInfo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel(private val auth: FirebaseAuth) : ViewModel() {
    private val chatListModel = ChatListModel()

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
            _novelInfo.value = chatListModel.getNovels(auth.currentUser?.uid ?: "")
        }
    }

    // Firestore에서 채팅을 삭제하는 함수
    fun deleteChatting(novelInfo: NovelInfo) {
        _documentID.value = novelInfo.documentID?:"ERROR"
        FirebaseTools.deleteDocument("NovelInfo", _documentID.value)
        fetchChatList()
    }

    // Firestore에서 채팅 목록을 불러오는 함수
    private fun fetchChatList() {
        viewModelScope.launch {
            _novelInfo.value = chatListModel.getNovels(auth.currentUser?.uid ?: "")
        }
    }
}