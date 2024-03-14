package com.example.villainlp.server

import android.content.ContentValues
import android.util.Log
import com.example.villainlp.novel.common.Book
import com.example.villainlp.shared.NovelInfo
import com.example.villainlp.shared.RelayNovel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirebaseTools {
    // Library, ReadBook
    suspend fun getCommentCount(documentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        val commentCollection = db.collection("Library").document(documentId).collection("comment")
        val snapshot = commentCollection.get().await()

        return snapshot.size()
    }

    // ReadMyNovel : Save Book
    fun saveAtFirebase(book: Book) {
        val db = FirebaseFirestore.getInstance()
        val newDocRef = db.collection("Library").document()
        val documentId = newDocRef.id
        val updateBook = book.copy(documentID = documentId)

        newDocRef.set(updateBook)
    }

    // CreateYard : Save NovelInfo
    fun saveAtFirebase(book: NovelInfo) {
        val db = FirebaseFirestore.getInstance()
        val newDocRef = db.collection("NovelInfo").document()
        val documentId = newDocRef.id
        val updateBook = book.copy(documentID = documentId)

        newDocRef.set(updateBook)
    }

    // Gemini, chat : Save ChatToNovel
    fun saveAtFirebase(book: RelayNovel) {
        val db = FirebaseFirestore.getInstance()
        val newDocRef = db.collection("MyBookData").document()
        val documentId = newDocRef.id
        val updateBook = book.copy(documentID = documentId)

        newDocRef.set(updateBook)
    }

    // Delete : MyNovel, ChatList
    fun deleteDocument(collectionPath: String, documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(collectionPath).document(documentId).delete()
    }

    // Chat : Report
    fun reportContent(content: String) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val email = user?.email ?: "Unknown User"

    val report = hashMapOf(
        "userEmail" to email,
        "AI 생성 컨텐츠" to content
    )

    db.collection("ChatReport")
        .add(report)
        .addOnSuccessListener { documentReference ->
            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding document", e)
        }
}
}
