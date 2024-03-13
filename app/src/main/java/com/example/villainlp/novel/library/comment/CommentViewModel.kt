package com.example.villainlp.novel.library.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {
    private val commentModel = CommentModel()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    private val _commentList = MutableStateFlow<List<Comment>>(emptyList())
    val commentList: StateFlow<List<Comment>> = _commentList

    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount

    private val _commentDocumentId = MutableStateFlow("")

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    private val _isAnimationPlaying = MutableStateFlow(false)
    val isAnimationPlaying: StateFlow<Boolean> = _isAnimationPlaying

    // 처음 Comment를 로드하는 부분
    fun loadComments(documentId: String) {
        viewModelScope.launch {
            _commentList.value = commentModel.getComments(documentId, user?.uid?:"ERROR")
            _commentCount.value = _commentList.value.size
        }
    }

    fun onReloadClicked(documentId: String){
        _isAnimationPlaying.value = !_isAnimationPlaying.value
        loadComments(documentId)
    }

    // Comment 삭제
    fun deleteComment(documentId: String, comment: Comment) {
        _commentDocumentId.value = comment.documentID ?: "ERROR"
        commentModel.deleteDocument(documentId, _commentDocumentId.value)
        viewModelScope.launch {
            _commentList.value = commentModel.getComments(documentId, user?.uid?:"ERROR")
            _commentCount.value = _commentList.value.size
            commentModel.updateCommentCount(documentId, _commentList.value.size)
        }
    }

    // comment의 값이 바뀌는 부분
    fun onCommentChanged(newComment: String) {
        _commentText.value = newComment
    }

    // Comment 초기화
    fun initComment() {
        _commentText.value = ""
    }

    fun onCommentSubmit(documentId: String){
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val myComment = Comment(
            author = user?.displayName ?: "ERROR",
            uploadDate = currentDate,
            script = commentText.value,
            userID = user?.uid ?: "ERROR"
        )
        commentModel.saveComment(documentId, myComment)
        loadComments(documentId)
        initComment()
    }
}