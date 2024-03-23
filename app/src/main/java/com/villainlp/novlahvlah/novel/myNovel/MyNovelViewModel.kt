package com.villainlp.novlahvlah.novel.myNovel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villainlp.novlahvlah.server.FirebaseTools
import com.villainlp.novlahvlah.shared.RelayNovel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MyNovelViewModel: ViewModel() {
    private val myNovelModel = MyNovelModel()

    private val _novelList = MutableStateFlow<List<RelayNovel>>(emptyList())
    val novelList: StateFlow<List<RelayNovel>> = _novelList

    private val _documentId = MutableStateFlow("")
    val documentId: StateFlow<String> = _documentId

    fun loadNovels(auth: FirebaseAuth){
        viewModelScope.launch {
            _novelList.value = myNovelModel.getNovels(auth.currentUser?.uid ?: "")
        }
    }

    fun onDeleteClicked(selectedNovel: RelayNovel, auth: FirebaseAuth){
        _documentId.value = selectedNovel.documentID ?: "ERROR"
        FirebaseTools.deleteDocument("MyBookData", _documentId.value)
        loadNovels(auth)
    }
}