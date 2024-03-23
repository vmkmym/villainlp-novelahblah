package com.villainlp.novlahvlah.novel.myNovel

import com.villainlp.novlahvlah.shared.RelayNovel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MyNovelModel {
    // MyNovel
    suspend fun getNovels(userId: String): List<RelayNovel> = coroutineScope {
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("MyBookData")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get().await().documents.mapNotNull { document ->
                    val myNovels = document.toObject(RelayNovel::class.java)
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

}
