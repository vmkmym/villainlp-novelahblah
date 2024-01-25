package com.example.villainlp.novel.myNovel.readMyNovel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.villainlp.novel.AlertPopup
import com.example.villainlp.novel.ReadTopBar

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
        AlertPopup(
            title = "작성한 소설 업로드",
            message = description,
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked(navController, title, script) },
            novelTitle = title,
            warningMessage = "이 작품을 도서관에 출품하시겠습니까?",
            hasTextField = true,
            tfLabel = "작품을 요약해서 써주세요",
            onTextFieldValueChange = { newDescription -> viewModel.onDescriptionChanged(newDescription) }
        )
    }
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
            ReadTopBar(
                title = title,
                navController = navController,
                onClicked = { onClicked() },
                hasIcon = true)
        },
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

