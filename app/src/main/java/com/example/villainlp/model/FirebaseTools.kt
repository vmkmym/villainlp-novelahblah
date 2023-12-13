package com.example.villainlp.model

import android.content.ContentValues
import android.util.Log
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

    suspend fun fetchDataFromFirestore(userId: String): List<Book> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val result = mutableListOf<Book>()

            db.collection("books")
                .get().addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val bookData = document.toObject(Book::class.java)
                        if (bookData.userID == userId) {
                            result.add(bookData)
                        }
                    }
                    continuation.resume(result)
                }.addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }
}