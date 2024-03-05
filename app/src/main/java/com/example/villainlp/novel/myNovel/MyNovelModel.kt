package com.example.villainlp.novel.myNovel

import com.example.villainlp.novel.RelayChatToNovelBook
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MyNovelModel {
    // MyNovel
    suspend fun getNovels(userId: String): List<RelayChatToNovelBook> = coroutineScope {
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

}
