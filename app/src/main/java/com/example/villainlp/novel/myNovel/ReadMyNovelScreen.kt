package com.example.villainlp.novel.myNovel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.example.villainlp.ui.theme.Blue789

@Composable
fun ReadMyBookScreen(
    navController: NavHostController,
    title: String,
    script: String,
    viewModel: ReadMyNovelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val showDialog by viewModel.showDialog.collectAsState()
    val description by viewModel.description.collectAsState()

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
    if (showDialog){
        UploadMyBookToLibrary(
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked(navController, title, script) },
            onDescriptionChange = { newDescription -> viewModel.onDescriptionChanged(newDescription) },
            title = title,
            description = description
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

// ReadMyNovel
@Composable
fun ReadMyBookScaffold(
    title: String,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
    onClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            ReadMyBookScaffoldTopBar(title, navController) { onClicked() }
        },
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

@Composable
fun ReadMyBookScaffoldTopBar(
    title: String,
    navController: NavHostController,
    onClicked: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(20.dp),
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "back"
            )
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF212121),
                )
            )
            Image(
                modifier = Modifier.clickable { onClicked() },
                painter = painterResource(id = R.drawable.file_upload),
                contentDescription = "upload"
            )
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}
