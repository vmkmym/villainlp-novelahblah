package com.example.villainlp.novel.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.novel.Book
import com.example.villainlp.server.FirebaseTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryViewModel:ViewModel() {
    private val _novelList = MutableStateFlow<List<Book>>(emptyList())
    val novelList: StateFlow<List<Book>> = _novelList

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

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
                    _novelList.value = FirebaseTools.novelDataSortingByRatingFromFirestore()
                }
                isViewClicked.value -> {
                    _novelList.value = FirebaseTools.novelDataSortingByViewsFromFirestore()
                }
                isUpdateClicked.value -> {
                    _novelList.value = FirebaseTools.novelDataSortingByUploadDateFromFirestore()
                }
                else -> {
                    _novelList.value = FirebaseTools.novelDataSortingByUploadDateFromFirestore()
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
        _showDialog.value = true
    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onConfirmClicked(){
        FirebaseTools.deleteLibraryBookFromFirestore(documentId.value)
        onDismissDialog()
    }
}