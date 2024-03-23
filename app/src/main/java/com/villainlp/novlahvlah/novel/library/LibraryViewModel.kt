package com.villainlp.novlahvlah.novel.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villainlp.novlahvlah.novel.common.Book
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel:ViewModel() {
    private val libraryModel = LibraryModel()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser?.uid?:"ERROR"

    private val _novelList = MutableStateFlow<List<Book>>(emptyList())
    val novelList: StateFlow<List<Book>> = _novelList

    private val _documentId = MutableStateFlow("")
    val documentId: StateFlow<String> = _documentId

    private val _isRateClicked = MutableStateFlow(false)
    val isRateClicked: StateFlow<Boolean> = _isRateClicked

    private val _isViewClicked = MutableStateFlow(false)
    val isViewClicked: StateFlow<Boolean> = _isViewClicked

    private val _isUpdateClicked = MutableStateFlow(false)
    val isUpdateClicked: StateFlow<Boolean> = _isUpdateClicked

    fun loadNovels(){
        viewModelScope.launch {
            when {
                isRateClicked.value -> {
                    _novelList.value = libraryModel.novelSort("rating", user)
                }
                isViewClicked.value -> {
                    _novelList.value = libraryModel.novelSort("views", user)
                }
                isUpdateClicked.value -> {
                    _novelList.value = libraryModel.novelSort("uploadDate", user)
                }
                else -> {
                    _novelList.value = libraryModel.novelSort("uploadDate", user)
                }
            }
        }
    }

    fun rateClicked(){
        _isRateClicked.value = true
        _isViewClicked.value = false
        _isUpdateClicked.value = false
    }

    fun viewClicked(){
        _isViewClicked.value = true
        _isRateClicked.value = false
        _isUpdateClicked.value = false
    }

    fun updateClicked(){
        _isUpdateClicked.value = true
        _isRateClicked.value = false
        _isViewClicked.value = false
    }

    fun onDeleteClicked(selectedNovel: Book){
        _documentId.value = selectedNovel.documentID ?: "ERROR"
        viewModelScope.launch {
            libraryModel.deleteDocument(documentId.value)
        }
        loadNovels()
    }
}