package com.example.villainlp.novel.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.novel.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel:ViewModel() {
    private val libraryModel = LibraryModel()

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
                    _novelList.value = libraryModel.novelSort("rating")
                }
                isViewClicked.value -> {
                    _novelList.value = libraryModel.novelSort("views")
                }
                isUpdateClicked.value -> {
                    _novelList.value = libraryModel.novelSort("uploadDate")
                }
                else -> {
                    _novelList.value = libraryModel.novelSort("uploadDate")
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