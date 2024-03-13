package com.example.villainlp.novel.report

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class BlackList(
    val user: String = "",
    val blackedID: String = "",
    val blackedName: String = ""
)

class ReportModel {
    suspend fun blackList(blackList: BlackList) {
        val db = FirebaseFirestore.getInstance()
        val blackRef = db.collection("BlackList").document(blackList.user)
        val snapshot = blackRef.get().await()

        if (snapshot.exists()) {
            // 문서가 이미 존재하는 경우 업데이트합니다
            updateBlackList(blackList, blackRef)
        } else {
            // 문서가 존재하지 않는 경우 추가합니다
            addBlackList(blackList, blackRef)
        }
    }

    private fun addBlackList(blackList: BlackList, blackRef: DocumentReference){
        val data = hashMapOf(blackList.blackedName to blackList.blackedID)
        blackRef.set(data)
    }

    private suspend fun updateBlackList(blackList: BlackList, blackRef: DocumentReference){
        // 필드를 추가합니다
        val updates = hashMapOf<String, Any>(blackList.blackedName to blackList.blackedID)
        blackRef.update(updates).await()
    }
}
