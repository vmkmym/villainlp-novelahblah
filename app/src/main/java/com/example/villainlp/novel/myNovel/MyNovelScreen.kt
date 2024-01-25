package com.example.villainlp.novel.myNovel

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.novel.DeleteAlert
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.shared.ShowMyBooks
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyBookScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: MyNovelViewModel = viewModel(),
) {
    val showDialog by viewModel.showDialog.collectAsState()
    val novelList by viewModel.novelList.collectAsState()

    viewModel.loadNovels(auth)

    MyScaffold("내서재", navController) {
        ShowMyBooks(it, navController, novelList) { selectedNovel ->
            viewModel.onDeleteClicked(selectedNovel)
        }

        if (showDialog) {
            DeleteAlert(
                title = "정말로 삭제하시겠습니까?",
                warningMessage = "내 작업 공간에서 선택한 소설이 삭제가 됩니다.",
                onDismiss = { viewModel.onDismissDialog() },
                onConfirm = { viewModel.onConfirmClicked(auth) }
            )
        }
    }
}
