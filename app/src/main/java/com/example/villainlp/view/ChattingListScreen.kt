package com.example.villainlp.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.FirebaseTools
import com.example.villainlp.model.NovelInfo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChattingListScreen(navController: NavHostController, auth: FirebaseAuth) {
    var showDialog by remember { mutableStateOf(false) }
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))
    var documentID by remember { mutableStateOf("") }

    var novelInfo by remember { mutableStateOf<List<NovelInfo>>(emptyList()) }
    val scope = rememberCoroutineScope()

    scope.launch {
        novelInfo = FirebaseTools.fetchNovelInfoDataFromFirestore(auth.currentUser?.uid ?: "")
    }

    MyScaffold("내 작업 공간", navController) {
        ShowChats(it, navController, novelInfo) { selectedChatting ->
            documentID = selectedChatting.documentID ?: "ERROR"
            showDialog = true
        }
    }
    if (showDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "정말로 삭제하시겠습니까?")
            },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(text = "내 작업공간에서 삭제가 됩니다.")
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        FirebaseTools.deleteChattingFromFirestore(documentID)
                        showDialog = false
                    }
                ) {
                    Text(text = "확인")
                }
            },
            dismissButton = {
                IconButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = "취소")
                }
            },
            modifier = Modifier
                .padding(16.dp)
        )
    }
}
