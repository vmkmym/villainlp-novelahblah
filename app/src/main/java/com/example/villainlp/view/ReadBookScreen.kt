package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReadLibraryBookScreen(
    navController: NavHostController,
    title: String,
    script: String,
    documentId: String,
) {
    ReadLibraryBookScaffold(title, navController, documentId) {
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
    }
}