package com.example.villainlp.novel.myNovel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.villainlp.novel.Book
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.Screen
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadMyBookViewModel : ViewModel() {

    // 필요한 데이터 및 메서드를 정의
    // 예시로서 필요한 데이터와 메서드만 추가, 실제로 필요한 것에 따라 수정 가능

    var showDialog by mutableStateOf(false)
    var description by mutableStateOf("")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    fun onDialogClicked(){
        showDialog = true
    }

    fun onDismissDialog(){
        showDialog = false
    }

    fun onDescriptionChanged(newDescription: String) {
        description = newDescription
    }

    fun onConfirmClicked(navController: NavHostController, title: String, script: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
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
}