package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools
import com.example.villainlp.model.Screen
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadMyBookScreen(
    navController: NavHostController,
    title: String,
    script: String,
    auth: FirebaseAuth,
) {
    var showDialog by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    val user = auth.currentUser

    ReadMyBookScaffold(
        title, navController,
        content = {
            LazyColumn(
                modifier = it,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item { Text(script) }
            }
        },
        onClicked = {
            showDialog = true
        }
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "글 올리기")
                }
            },
            text = {
                Column {
                    Text(text = "\"${title}\"이 작품을 도서관에 출품하시겠습니까?")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("작품을 요약해서 써주세요") },
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                            Date()
                        )
                        val book = Book(
                            title = title,
                            author = user?.displayName ?: "ERROR",
                            description = description,
                            script = script,
                            userID = user?.uid ?: "ERROR",
                            uploadDate = currentDate
                        )
                        FirebaseTools.saveBook(book)
                        showDialog = false
                        navController.navigate(Screen.Library.route)
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