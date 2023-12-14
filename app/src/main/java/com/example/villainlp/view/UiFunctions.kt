package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools
import com.example.villainlp.model.Screen
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffold(
    title: String,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            MyScaffoldTopBar(title)
        },
        bottomBar = {
            MyScaffoldBottomBar(navController)
        }
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

@Composable
fun MyScaffoldBottomBar(navController: NavHostController) {
    Row(
        Modifier
            .shadow(
                elevation = 16.dp,
                spotColor = Color(0x3817C3CE),
                ambientColor = Color(0x3817C3CE)
            )
            .width(428.dp)
            .height(100.dp)
            .background(color = Color(0xFFF4F4F4))
            .padding(start = 33.dp, top = 21.dp, end = 33.dp, bottom = 21.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        // Child views.
        CustomIconButton(R.drawable.messages, "창작마당") { navController.navigate(Screen.Home.route) }
        CustomIconButton(
            R.drawable.archive_add,
            "내서재"
        ) { navController.navigate(Screen.MyBook.route) }
        CustomIconButton(
            R.drawable.outline_book_24,
            "도서관"
        ) { navController.navigate(Screen.Library.route) }
        CustomIconButton(
            R.drawable.outline_build_24,
            "설정"
        ) { navController.navigate(Screen.Settings.route) }
    }
}

@Composable
fun CustomIconButton(imageId: Int, iconText: String, onClicked: () -> Unit) {
    IconButton(onClick = { onClicked() }) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier
                    .padding(0.dp)
                    .width(28.dp)
                    .height(28.dp),
                painter = painterResource(id = imageId),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .height(17.dp),
                text = iconText,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFBBBBBB),
                )
            )
        }
    }
}

@Composable
fun MyScaffoldTopBar(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF212121),
                )
            )
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}

@Composable
fun SaveNovelBotton(navController: NavHostController, user: FirebaseUser?) {
    Button(
        onClick = {
            val book = Book(
                title = "title",
                author = user!!.displayName!!,
                description = "description",
                userID = user.uid,
            )
            FirebaseTools.saveBook(book)
            navController.navigate("SavedBook")
        }
    ) {
        Text("Save Book")
    }
}

@Composable
fun ShowBooks(user: FirebaseUser?, modifier: Modifier, navController: NavHostController) {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    LaunchedEffect(Unit) {
        books = FirebaseTools.fetchDataFromFirestore(user?.uid ?: "")
    }
    LazyColumn(
        modifier = modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(books) { book ->
            BookCards(book, navController)
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

@Composable
fun BookCards(book: Book, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(378.dp)
            .height(95.dp)
            .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(size = 19.dp))
            .clickable { navController.navigate("ReadBookScreen/${book.title}/${book.description}") }
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .width(322.dp)
                    .height(22.dp),
                text = book.title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF212121),
                )
            )
            Text(
                modifier = Modifier
                    .width(322.dp)
                    .height(30.dp),
                text = book.description,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFFBBBBBB),
                )
            )
            Text(
                modifier = Modifier
                    .width(62.dp)
                    .height(12.dp)
                    .align(Alignment.End),
                text = book.author,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.End
                )
            )
        }
    }
}