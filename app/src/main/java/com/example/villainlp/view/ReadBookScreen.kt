package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

@Composable
fun ReadBookScreen(navController: NavHostController, title: String, discription: String, documentId: String) {
    MyReadBookScaffold(title, navController, documentId) {
        LazyColumn(
            modifier = it,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item { Text(discription) }
        }
    }
}