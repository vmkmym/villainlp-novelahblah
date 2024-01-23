package com.example.villainlp.novel.myNovel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.shared.ReadMyBookScaffold
import com.example.villainlp.ui.theme.Blue789

@Composable
fun ReadMyBookScreen(
    navController: NavHostController,
    title: String,
    script: String,
    viewModel: ReadMyBookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    ReadMyBookScaffold(
        title, navController,
        content = {
            LazyColumn(
                modifier = it,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Text(
                        text = script,
                        modifier = Modifier.padding(26.dp),
                        color = Color.Black,
                    )
                }
            }
        },
        onClicked = {
            viewModel.onDialogClicked()
        }
    )
    if (viewModel.showDialog){
        UploadMyBookToLibrary(
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked(navController, title, script) },
            onDescriptionChange = { newDescription -> viewModel.onDescriptionChanged(newDescription) },
            title = title,
            description = viewModel.description
        )
    }
}

@Composable
fun UploadMyBookToLibrary(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    title: String,
    description: String,
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "작성한 소설 업로드",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "\"${title}\"이 작품을 도서관에 출품하시겠습니까?",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { onDescriptionChange(it) },
                    modifier = Modifier
                        .width(300.dp)
                        .height(80.dp)
                        .padding(8.dp),
                    label = {
                        Text("작품을 요약해서 써주세요")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue789,
                        unfocusedBorderColor = Blue789
                    ),
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
        modifier = Modifier
            .padding(16.dp)
    )

}