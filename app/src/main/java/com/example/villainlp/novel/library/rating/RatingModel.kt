package com.example.villainlp.novel.library.rating

import com.example.villainlp.novel.common.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

data class RatingOption(
    val artistryRate: Int,
    val originalityRate: Int,
    val commercialViabilityRate: Int,
    val literaryMeritRate: Int,
    val completenessRate: Int
)

enum class StarFactors(val factor: String){
    Artistry("작품성 (문장력과 구성력)"),
    Original("독창성 (창조성과 기발함)"),
    Commercial("상업성 (몰입도와 호소력)"),
    Literary("문학성 (철학과 감명)"),
    Complete("완성도 (문체의 가벼움과 진지함)")
}

class RatingModel {
    // Rating
    fun updateRating(documentId: String, totalRate: Float, starCount: Int) {
        val db = FirebaseFirestore.getInstance()
        val documentReference = db.collection("Library").document(documentId)

        documentReference.get().addOnSuccessListener {
            val rate = totalRate / starCount

            val updates = hashMapOf<String, Any>(
                "totalRate" to totalRate,
                "starCount" to starCount,
                "rating" to rate
            )
            documentReference.update(updates)
        }
    }

    // Rating
    suspend fun getBook(documentId: String): List<Book> = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        try {
            val documentSnapShot = db.collection("Library").document(documentId).get().await()
            val book = documentSnapShot.toObject(Book::class.java)
            if(book != null){
                listOf(book)
            }else{
                emptyList()
            }

        } catch (e: Exception){
            println("Error getting documents: $e")
            emptyList()
        }
    }
}