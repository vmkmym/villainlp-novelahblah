package com.example.villainlp.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser

@Composable
fun MyBookScreen(user: FirebaseUser?, navController: NavHostController) {
    MyScaffold("Library", navController) { ShowBooks(user, it, navController) }
}