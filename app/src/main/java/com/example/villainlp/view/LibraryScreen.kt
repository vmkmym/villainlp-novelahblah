package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools.saveBook
import com.google.firebase.auth.FirebaseUser

@Composable
fun BookScreen(navController: NavHostController, user: FirebaseUser?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Book Screen")
        Button(
            onClick = {
                val book = Book(
                    title = "title",
                    author = user!!.displayName!!,
                    description = "description",
                    userID = user.uid,
                )
                saveBook(book)
                navController.navigate("SavedBook")
            }
        ) {
            Text("Save Book")
        }
    }
}