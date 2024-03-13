package com.example.villainlp.setting

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.novel.report.DefaultTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BlockManageScreen(auth: FirebaseAuth, navController: NavHostController) {
    val currentUser = auth.currentUser?.uid ?: "ERROR"
    var blackList by remember { mutableStateOf(emptyList<BlockedData>()) }
    val isDarkTheme = isSystemInDarkTheme()

    val scope = rememberCoroutineScope()
    scope.launch {
        blackList = getBlackedIDs(currentUser)
    }
    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "차단 목록",
                navController = navController,
                isDarkTheme = isDarkTheme
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(blackList, key = { item -> item.uid }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name)
                        OutlinedButton(onClick = { unblock(currentUser, it.uid) }) {
                            Text(
                                text = "차단 해제",
                                fontFamily = FontFamily(Font(R.font.yeongdeok_sea))
                            )
                        }
                    }
                }
                Divider()
            }
        }
    }
}

data class BlockedData(
    val name: String = "",
    val uid: String = "",
)

private suspend fun getBlackedIDs(user: String): List<BlockedData> =
    coroutineScope {
        val db = FirebaseFirestore.getInstance()

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

fun unblock(user: String, blockID: String) {
    val db = FirebaseFirestore.getInstance()
    val documentRef = db.collection("BlackList").document(user)

    // 해당 값(value)을 가진 필드의 key를 찾습니다.
    documentRef.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            val data = snapshot.data
            val keyToDelete = data?.entries?.find { it.value == blockID }?.key

            // 해당 key를 사용하여 필드를 삭제합니다.
            if (keyToDelete != null) {
                val updates = hashMapOf<String, Any>(keyToDelete to FieldValue.delete())
                documentRef.update(updates)
            }
        }
    }
}