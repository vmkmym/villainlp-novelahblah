package com.example.villainlp.server

import com.example.villainlp.novel.Book
import com.example.villainlp.novel.NovelInfo
import com.example.villainlp.novel.RelayChatToNovelBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

object FirebaseTools {
    // Library, ReadBook
    suspend fun getCommentCount(documentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        val commentCollection = db.collection("Library").document(documentId).collection("comment")
        val snapshot = commentCollection.get().await()

        return snapshot.size()
    }

    // MyNovel
    suspend fun myNovelDataFromFirestore(userId: String): List<RelayChatToNovelBook> = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("MyBookData")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { document ->
                    val myNovels = document.toObject(RelayChatToNovelBook::class.java)
                    if (myNovels?.userID == userId) {
                        myNovels
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            println("Error getting documents: $e")
            emptyList()
        }
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
