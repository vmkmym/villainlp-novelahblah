package com.villainlp.novlahvlah.setting.blockManage

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

data class BlockedData(
    val name: String = "",
    val uid: String = "",
)

class BlockManageModel {
    private val db = FirebaseFirestore.getInstance()
    suspend fun getBlackedIDs(user: String): List<BlockedData> =
        coroutineScope {

            try {
                val snapshot = db.collection("BlackList")
                    .document(user).get().await()

                if (snapshot.exists()) {
                    // 문서가 존재하는 경우, 데이터를 BlockedData 객체로 변환하여 리스트에 추가합니다.
                    snapshot.data?.map { (name, uid) ->
                        BlockedData(name = name, uid = uid as String)
                    }?.toList() ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                println("Error getting blacked IDs: $e")
                emptyList()
            }
        }

    suspend fun deleteBlockID(user: String, blockID: String) {
        val documentRef = db.collection("BlackList").document(user)

        // 해당 값(value)을 가진 필드의 key를 찾습니다.
        try {
            val snapshot = documentRef.get().await()
            if (snapshot.exists()) {
                val data = snapshot.data
                val keyToDelete = data?.entries?.find { it.value == blockID }?.key

                // 해당 key를 사용하여 필드를 삭제합니다.
                if (keyToDelete != null) {
                    val updates = hashMapOf<String, Any>(keyToDelete to FieldValue.delete())
                    documentRef.update(updates)
                }
            }

        } catch (e: Exception) {
            Log.d("deleteBlockID", "오류 : $e")
        }
    }
}
