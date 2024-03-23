package com.villainlp.novlahvlah.novel.library.readBook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villainlp.novlahvlah.novel.common.formatRating
import com.villainlp.novlahvlah.server.FirebaseTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReadBookViewModel: ViewModel() {
    private val readBookModel = ReadBookModel()

    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount

    private val _rating = MutableStateFlow(0.0f)
    var rating: StateFlow<Float> = _rating

    private val _barVisible = MutableStateFlow(true)
    var barVisible: StateFlow<Boolean> = _barVisible

    fun reloadCommentRating(documentId: String){
        viewModelScope.launch {
            _commentCount.value = FirebaseTools.getCommentCount(documentId)
            _rating.value = formatRating(readBookModel.getRating(documentId)!!)
        }
    }

    fun onTap(){
        _barVisible.value = !_barVisible.value
    }

    fun viewsPlus(documentId: String, views: String){
        val updateView = views.toInt() + 1
        readBookModel.viewsUpdate(documentId, updateView)
    }
}