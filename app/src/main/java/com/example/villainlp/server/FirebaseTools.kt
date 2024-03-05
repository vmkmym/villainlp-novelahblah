package com.example.villainlp.server

import com.example.villainlp.novel.Book
import com.example.villainlp.novel.NovelInfo
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.novel.library.comment.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

object FirebaseTools {

    // Library
    suspend fun deleteLibraryBookFromFirestore(documentId: String) {
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
    suspend fun novelSort(sortOption: String): List<Book> = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("Library")
                .orderBy(sortOption, Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { document ->
                    val novelInfo = document.toObject(Book::class.java)
                    novelInfo
                }
        } catch (e: Exception) {
            println("Error getting documents: $e")
            emptyList()
        }
    }

    // Library, ReadBook
    suspend fun getCommentCount(documentId: String): Int {
        val db = FirebaseFirestore.getInstance()
        val commentCollection = db.collection("Library").document(documentId).collection("comment")
        val snapshot = commentCollection.get().await()

        return snapshot.size()
    }

    // Comment
    fun updateCommentCount(documentId: String, commentCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("Library").document(documentId)
        val updates = hashMapOf<String, Any>("commentCount" to commentCount)
        documentReference.update(updates)
    }

    // Comment
    fun uploadComment(documentId: String, comment: Comment) {
        val db = FirebaseFirestore.getInstance()
        val newDocRef = db.collection("Library").document(documentId).collection("comment").document()
        val commentDocumentId = newDocRef.id
        val updateComment = comment.copy(documentID = commentDocumentId)
        newDocRef.set(updateComment)
    }

    // Comment
    suspend fun fetchCommentsFromFirestore(documentId: String): List<Comment> =
        coroutineScope {
            val db = FirebaseFirestore.getInstance()

            try {
                db.collection("Library").document(documentId).collection("comment")
                    .orderBy("uploadDate", Query.Direction.ASCENDING)
                    .get().await().documents.mapNotNull { document ->
                        val comments = document.toObject(Comment::class.java)
                        comments
                    }
            } catch (e: Exception){
                println("Error getting documents: $e")
                emptyList()
            }
        }

    // Comment
    fun deleteCommentFromFirestore(documentId: String, commentDocumentId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Library").document(documentId).collection("comment").document(commentDocumentId).delete()
    }

    // ChattingList
    suspend fun fetchNovelInfoDataFromFirestore(userId: String): List<NovelInfo> = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("NovelInfo")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { document ->
                    val novelInfo = document.toObject(NovelInfo::class.java)
                    if (novelInfo?.userID == userId) {
                        novelInfo
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            println("Error getting documents: $e")
            emptyList()
        }
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
