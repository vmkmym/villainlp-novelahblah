package com.example.villainlp.model

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseTools {

    fun saveBook(book: Book) {
        val db = FirebaseFirestore.getInstance()

        val newDocRef = db.collection("Library").document()
        val documentId = newDocRef.id
        val updateBook = book.copy(documentID = documentId)
        newDocRef.set(updateBook)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun updateBookRating(documentId: String, newRating: Float) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("Library").document(documentId)
        // 업데이트할 데이터 생성
        val updates = hashMapOf<String, Any>(
            "rating" to newRating // 새로운 평점 값으로 업데이트
        )
        // 문서 업데이트
        documentReference.update(updates)
            .addOnSuccessListener {
                // 업데이트 성공
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot updated with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                // 업데이트 실패
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun updateBookViews(documentId: String, views: Int) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("Library").document(documentId)
        // 업데이트할 데이터 생성
        val updates = hashMapOf<String, Any>(
            "views" to views // 새로운 평점 값으로 업데이트
        )
        // 문서 업데이트
        documentReference.update(updates)
            .addOnSuccessListener {
                // 업데이트 성공
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot updated with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                // 업데이트 실패
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }


    fun saveNovelInfo(novelInfo: NovelInfo) {
        val db = FirebaseFirestore.getInstance()

        // Add a new document
        val newDocRef = db.collection("NovelInfo").document()

        // Get the ID of the new document
        val documentId = newDocRef.id

        // Update the novelInfo object with the document ID
        val updatedNovelInfo = novelInfo.copy(documentID = documentId)

        // Set the document data
        newDocRef.set(updatedNovelInfo)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun saveChatToNovel(relayChatToNovel: RelayChatToNovelBook) {
        val db = FirebaseFirestore.getInstance()

        val newDocRef = db.collection("MyBookData").document()
        val documentId = newDocRef.id
        val updateRelayChatToNovel = relayChatToNovel.copy(documentID = documentId)
        newDocRef.set(updateRelayChatToNovel)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $documentId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }


    fun deleteBookFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("MyBookData").document(documentId)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun deleteChattingFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("NovelInfo").document(documentId)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }

    fun deleteLibraryBookFromFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Library").document(documentId)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
    }


    suspend fun fetchNovelInfoDataFromFirestore(userId: String): List<NovelInfo> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<NovelInfo>()

            db.collection("NovelInfo")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val novelInfoId = document.id
                        val novelInfo = document.toObject(NovelInfo::class.java)
                        if (novelInfo.userID == userId) {
                            val bookWithId = NovelInfo(
                                novelInfo.title,
                                novelInfo.assistId,
                                novelInfo.threadId,
                                novelInfo.userID,
                                novelInfo.createdDate,
                                novelInfoId
                            )
                            result.add(bookWithId)
                        }
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun myNovelDataFromFirestore(userId: String): List<RelayChatToNovelBook> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<RelayChatToNovelBook>()

            db.collection("MyBookData")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(RelayChatToNovelBook::class.java)
                        if (bookData.userID == userId) {
                            val bookWithId = RelayChatToNovelBook(
                                bookData.title,
                                bookData.author,
                                bookData.script,
                                bookData.userID,
                                bookData.rating,
                                bookData.createdDate,
                                bookId
                            )
                            result.add(bookWithId)
                        }
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun novelDataSortingByRatingFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(Book::class.java)
                        val bookWithId = Book(
                            bookData.title,
                            bookData.author,
                            bookData.description,
                            bookData.script,
                            bookData.userID,
                            bookData.rating,
                            bookData.views,
                            bookData.uploadDate,
                            bookId
                        )
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }

    suspend fun novelDataSortingByUploadDateFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(Book::class.java)
                        val bookWithId = Book(
                            bookData.title,
                            bookData.author,
                            bookData.description,
                            bookData.script,
                            bookData.userID,
                            bookData.rating,
                            bookData.views,
                            bookData.uploadDate,
                            bookId
                        )
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }


    suspend fun novelDataSortingByViewsFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("Library")
                .orderBy("views", Query.Direction.DESCENDING)
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(Book::class.java)
                        val bookWithId = Book(
                            bookData.title,
                            bookData.author,
                            bookData.description,
                            bookData.script,
                            bookData.userID,
                            bookData.rating,
                            bookData.views,
                            bookData.uploadDate,
                            bookId
                        )
                        result.add(bookWithId)
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }
}