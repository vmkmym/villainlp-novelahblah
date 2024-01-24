package com.example.villainlp.novel.library.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.villainlp.server.FirebaseTools
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    private val _commentList = MutableStateFlow<List<Comment>>(emptyList())
    val commentList: StateFlow<List<Comment>> = _commentList

    private val _commentCount = MutableStateFlow(0)
    val commentCount: StateFlow<Int> = _commentCount

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _commentDocumentId = MutableStateFlow("")

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    // 처음 Comment를 로드하는 부분
    fun loadComments(documentId: String) {
        viewModelScope.launch {
            _commentList.value = FirebaseTools.fetchCommentsFromFirestore(documentId)
            _commentCount.value = _commentList.value.size
        }
    }

    // Comment 삭제
    fun deleteComment(documentId: String) {
        FirebaseTools.deleteCommentFromFirestore(documentId, _commentDocumentId.value)
        viewModelScope.launch {
            _commentList.value = FirebaseTools.fetchCommentsFromFirestore(documentId)
            _commentCount.value = _commentList.value.size
            FirebaseTools.updateCommentCount(documentId, _commentList.value.size)
        }
        _showDialog.value = false
    }

    // comment의 값이 바뀌는 부분
    fun onCommentChanged(newComment: String) {
        _commentText.value = newComment
    }

    // Comment 초기화
    fun initComment() {
        _commentText.value = ""
    }

    // 삭제버튼을 클릭했을때 값 변경
    fun onCommentClicked(comment: Comment) {
        _commentDocumentId.value = comment.documentID ?: "ERROR"
        _showDialog.value = true
    }

    // 취소버튼 누를때 값 변경
    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onCommentSubmit(documentId: String){
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val myComment = Comment(
            author = user?.displayName ?: "ERROR",
            uploadDate = currentDate,
            script = commentText.value,
            userID = user?.uid ?: "ERROR"
        )
        FirebaseTools.uploadComment(documentId, myComment)
        loadComments(documentId)
        initComment()
    }
}