package com.example.villainlp.novel.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.novel.formatRating
import com.example.villainlp.server.FirebaseTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReadBookViewModel: ViewModel() {
    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount

    private val _rating = MutableStateFlow(0.0f)
    var rating: StateFlow<Float> = _rating

    private val _barVisible = MutableStateFlow(true)
    var barVisible: StateFlow<Boolean> = _barVisible

    fun reloadCommentRating(documentId: String){
        viewModelScope.launch {
            _commentCount.value = FirebaseTools.getCommentCount(documentId)
            _rating.value = formatRating(FirebaseTools.getRatingFromFirestore(documentId)!!)
        }
    }

    fun onTap(){
        _barVisible.value = !_barVisible.value
    }

}