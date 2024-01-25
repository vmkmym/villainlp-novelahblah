package com.example.villainlp.novel.myNovel.readMyNovel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.villainlp.novel.Book
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReadMyNovelViewModel : ViewModel() {

    // 필요한 데이터 및 메서드를 정의
    // 예시로서 필요한 데이터와 메서드만 추가, 실제로 필요한 것에 따라 수정 가능

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    // auth를 선언해 navigation에서 할당하지 않아도됨
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser

    // Alert 내부값들
    fun onDialogClicked(){
        _showDialog.value = true
    }

    fun onDismissDialog(){
        _showDialog.value = false
    }

    fun onDescriptionChanged(newDescription: String) {
        _description.value = newDescription
    }

    fun onConfirmClicked(navController: NavHostController, title: String, script: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val book = Book(
            title = title,
            author = user?.displayName ?: "ERROR",
            description = description.value,
            script = script,
            userID = user?.uid ?: "ERROR",
            uploadDate = currentDate
        )
        FirebaseTools.saveBook(book)
        _showDialog.value = false
        navController.navigate(Screen.Library.route)
    }
}