package com.example.villainlp.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser

@Composable
fun ChattingListScreen(navController: NavHostController, user: FirebaseUser?){
    MyScaffold("내 작업 공간", navController ) { ShowChats(user, it, navController) }
}
