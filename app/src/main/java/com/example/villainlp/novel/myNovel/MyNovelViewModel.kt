package com.example.villainlp.novel.myNovel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.server.FirebaseTools
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyNovelViewModel: ViewModel() {
    private val _novelList = MutableStateFlow<List<RelayChatToNovelBook>>(emptyList())
    val novelList: StateFlow<List<RelayChatToNovelBook>> = _novelList

    private val _documentId = MutableStateFlow("")
    val documentId: StateFlow<String> = _documentId

    fun loadNovels(auth: FirebaseAuth){
        viewModelScope.launch {
            _novelList.value = FirebaseTools.myNovelDataFromFirestore(auth.currentUser?.uid ?: "")
        }
    }

    fun onDeleteClicked(selectedNovel: RelayChatToNovelBook, auth: FirebaseAuth){
        _documentId.value = selectedNovel.documentID ?: "ERROR"
        FirebaseTools.deleteDocument("MyBookData", _documentId.value)
        loadNovels(auth)
    }
}