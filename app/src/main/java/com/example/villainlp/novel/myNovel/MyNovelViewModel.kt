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

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _documentId = MutableStateFlow("")
    val documentId: StateFlow<String> = _documentId

    fun loadNovels(auth: FirebaseAuth){
        viewModelScope.launch {
            _novelList.value = FirebaseTools.myNovelDataFromFirestore(auth.currentUser?.uid ?: "")
        }
    }

    fun onDeleteClicked(selectedNovel: RelayChatToNovelBook){
        _documentId.value = selectedNovel.documentID ?: "ERROR"
        _showDialog.value = true
    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onConfirmClicked(auth: FirebaseAuth){
        FirebaseTools.deleteBookFromFirestore(documentId.value)
        loadNovels(auth)
        onDismissDialog()
    }
}