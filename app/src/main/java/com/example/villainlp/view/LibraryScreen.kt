package com.example.villainlp.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LibraryScreen(navController: NavHostController) {
    MyScaffold("도서관", navController) { ShowAllBooks(it, navController) }
}