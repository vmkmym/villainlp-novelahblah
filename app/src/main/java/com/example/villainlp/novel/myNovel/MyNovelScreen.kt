package com.example.villainlp.novel.myNovel

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.shared.ShowMyBooks
import com.example.villainlp.ui.theme.Blue789
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
            DeleteNovel(
                onDismiss = { viewModel.onDismissDialog() },
                onConfirm = { viewModel.onConfirmClicked(auth) }
                )
        }
    }
}

@Composable
fun DeleteNovel(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))
    AlertDialog(
        icon = {
            LottieAnimation(
                modifier = Modifier.size(40.dp),
                composition = firePuppleLottie,
                iterations = LottieConstants.IterateForever
            )
        },
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "정말로 삭제하시겠습니까?",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            )
        },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "내 작업 공간에서 선택한 소설이 삭제가 됩니다.",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            IconButton(
                onClick = { onConfirm() }
            ) {
                Text(
                    text = "확인",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
        },
        dismissButton = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = "취소",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )

}




