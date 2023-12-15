package com.example.villainlp.model

import android.content.ContentValues
import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseTools {
    fun saveBook(book: Book) {
        val db = FirebaseFirestore.getInstance()

        db.collection("books")
            .add(book)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun updateBookRating(documentId: String, newRating: Float) {
        val db = FirebaseFirestore.getInstance()
        // 업데이트할 문서의 참조 가져오기
        val documentReference = db.collection("books").document(documentId)
        // 업데이트할 데이터 생성
        val updates = hashMapOf<String, Any>(
            "rating" to newRating // 새로운 평점 값으로 업데이트
        )
        // 문서 업데이트
        documentReference.update(updates)
            .addOnSuccessListener {
                // 업데이트 성공
                Log.d(ContentValues.TAG, "DocumentSnapshot updated with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // 업데이트 실패
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun saveNovelInfo(novelInfo: NovelInfo) {
        val db = FirebaseFirestore.getInstance()

        db.collection("NovelInfo")
            .add(novelInfo)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    suspend fun fetchDataFromFirestore(userId: String): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("books")
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(Book::class.java)
                        if (bookData.userID == userId) {
                            val bookWithId = Book(
                                bookData.title,
                                bookData.author,
                                bookData.description,
                                bookData.userID,
                                bookData.rating,
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

    suspend fun bookDataFromFirestore(): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("books")
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookId = document.id
                        val bookData = document.toObject(Book::class.java)
                        val bookWithId = Book(
                            bookData.title,
                            bookData.author,
                            bookData.description,
                            bookData.userID,
                            bookData.rating,
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


    suspend fun fetchNovelInfoDataFromFirestore(userId: String): List<NovelInfo> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<NovelInfo>()

            db.collection("NovelInfo")
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

}