package com.example.villainlp.novel.library

import com.example.villainlp.novel.common.Book
import com.example.villainlp.server.FirebaseTools.getBlackedIDs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class LibraryModel{
    // Library
    suspend fun deleteDocument(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            // 문서를 삭제합니다.
            db.collection("Library").document(documentId).delete().await()

            // 서브컬렉션의 모든 문서를 삭제합니다.
            val subCollectionRef = db.collection("Library").document(documentId).collection("comment")
            val querySnapshot = subCollectionRef.get().await()
            for (document in querySnapshot.documents) {
                subCollectionRef.document(document.id).delete().await()
            }
        } catch (e: Exception) {
            println("Error deleting document and subcollection: $e")
        }
    }

    // Library
    suspend fun novelSort(sortOption: String, currentUser: String): List<Book> = coroutineScope {
        val db = FirebaseFirestore.getInstance()
        val blackedIDs = getBlackedIDs(currentUser)

        try {
            db.collection("Library")
                .orderBy(sortOption, Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { document ->
                    val book = document.toObject(Book::class.java)
                    if (book != null && book.userID !in blackedIDs){
                        book
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            println("Error getting documents: $e")
            emptyList()
        }
    }
}