package com.example.villainlp.novel.library

import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class BottomBarString(val string: String){
    StarImageDescription("별"),
    CommentImageDescription("댓글"),
    RatingText("별점주기")
}

class ReadBookModel {

    // ReadBook
    fun viewsUpdate(documentId: String, views: Int) {
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("Library").document(documentId)
        val updates = hashMapOf<String, Any>("views" to views)

        documentReference.update(updates)
    }

    // ReadBook
    suspend fun getRating(documentId: String): Float? =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()

            db.collection("Library").document(documentId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val rating = documentSnapshot.getDouble("rating")?.toFloat()
                    continuation.resume(rating)
                }
        }
}