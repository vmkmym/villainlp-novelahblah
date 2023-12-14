package com.example.villainlp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools.fetchDataFromFirestore
import com.google.firebase.auth.FirebaseUser

@Composable
fun SavedBooks(user: FirebaseUser?) {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    LaunchedEffect(Unit) {
        books = fetchDataFromFirestore(user?.uid ?: "")
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                Column {
                    Text(text = book.title)
                    Text(text = book.description)
                    Text(text = book.author)
                }
            }
        }
    }
}