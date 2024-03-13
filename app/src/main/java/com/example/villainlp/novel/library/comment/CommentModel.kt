package com.example.villainlp.novel.library.comment

import com.example.villainlp.server.FirebaseTools.getBlackedIDs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

data class Comment(
    val author: String = "",
    val uploadDate: String = "",
    val script: String = "",
    val userID: String = "",
    val documentID: String? = null
)

enum class BottomText(val text: String){
    On("코멘트를 작성해주세요 :)"),
    Off("주제와 무관한 내용 및 악플은 삭제될 수 있습니다."),
    Submit("등록")
}

enum class LimitChar(var n: Int){
    Max(500)
}

class CommentModel {
    // Comment
    fun updateCommentCount(documentId: String, commentCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("Library").document(documentId)
        val updates = hashMapOf<String, Any>("commentCount" to commentCount)
        documentReference.update(updates)
    }

    // Comment
    fun saveComment(documentId: String, comment: Comment) {
        val db = FirebaseFirestore.getInstance()
        val newDocRef = db.collection("Library").document(documentId).collection("comment").document()
        val commentDocumentId = newDocRef.id
        val updateComment = comment.copy(documentID = commentDocumentId)
        newDocRef.set(updateComment)
    }

    // Comment
    suspend fun getComments(documentId: String, currentUser: String): List<Comment> =
        coroutineScope {
            val db = FirebaseFirestore.getInstance()
            val blackedIDs = getBlackedIDs(currentUser)

            try {
                db.collection("Library").document(documentId).collection("comment")
                    .orderBy("uploadDate", Query.Direction.ASCENDING)
                    .get().await().documents.mapNotNull { document ->
                        val comment = document.toObject(Comment::class.java)
                        // 만약 blackedIDs에 해당하는 사용자가 아니라면 해당 코멘트를 반환합니다
                        if (comment != null && comment.userID !in blackedIDs) {
                            comment
                        } else {
                            null
                        }
                    }
            } catch (e: Exception){
                println("Error getting documents: $e")
                emptyList()
            }
        }

    // Comment
    fun deleteDocument(documentId: String, commentDocumentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Library").document(documentId).collection("comment").document(commentDocumentId).delete()
    }

}