package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

@Composable
fun ReadLibraryBookScreen(navController: NavHostController, title: String, script: String, documentId: String, rating: Float?) {
    ReadLibraryBookScaffold(title, navController, documentId, rating) { modifier, listState ->
        LazyColumn(
            modifier = modifier,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item { Text(script) }
        }
    }
}