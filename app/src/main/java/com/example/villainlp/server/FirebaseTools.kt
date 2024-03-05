package com.example.villainlp.server

import com.example.villainlp.novel.common.Book
import com.example.villainlp.shared.NovelInfo
import com.example.villainlp.shared.RelayChatToNovelBook
import com.google.firebase.firestore.FirebaseFirestore
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
    fun saveAtFirebase(book: RelayChatToNovelBook) {
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
}
