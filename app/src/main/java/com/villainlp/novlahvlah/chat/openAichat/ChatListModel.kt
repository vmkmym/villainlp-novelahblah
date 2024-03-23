package com.villainlp.novlahvlah.chat.openAichat

import com.villainlp.novlahvlah.shared.NovelInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class ChatListModel {
    // ChattingList
    suspend fun getNovels(userId: String): List<NovelInfo> = coroutineScope {
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
}