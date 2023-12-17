package com.example.villainlp.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LibraryScreen(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current

    val sortOptions = listOf(R.drawable.ic_star_filled, R.drawable.views, R.drawable.clock)

    var showDialog by remember { mutableStateOf(false) }
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))
    var documentID by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val user = auth.currentUser
    var isCurrentUser by remember { mutableStateOf(false) }

    scope.launch {
        books = FirebaseTools.novelDataSortingByViewsFromFirestore()
    }
    MyLibraryScaffold(
        "도서관", navController
    ) {
        Column(modifier = it.padding(top = 12.dp)) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(sortOptions.size) { index ->
                    SortOptionButton(sortOptions[index]) {
                        scope.launch {
                            books = when (index) {
                                0 -> FirebaseTools.novelDataSortingByRatingFromFirestore()
                                1 -> FirebaseTools.novelDataSortingByViewsFromFirestore()
                                2 -> FirebaseTools.novelDataSortingByUploadDateFromFirestore()
                                else -> emptyList()
                            }
                        }
                    }
                }
            }
            ShowAllBooks(navController, books) { selectedBook ->
                documentID = selectedBook.documentID ?: "ERROR"
                isCurrentUser = user?.uid == selectedBook.userID
                if (isCurrentUser) {
                    showDialog = true
                } else {
                    showToasts("작성자만 삭제할 수 있습니다.", context)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "정말로 삭제하시겠습니까?")
            },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(text = "내 서재에서 삭제가 됩니다.")
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        FirebaseTools.deleteLibraryBookFromFirestore(documentID)
                        showDialog = false
                    }
                ) { Text(text = "확인") }
            },
            dismissButton = {
                IconButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = "취소")
                }
            },
            modifier = Modifier
                .padding(16.dp)
        )
    }
}


fun showToasts(message: String, context: Context) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    toast.show()
}

@Composable
fun SortOptionButton(image: Int, onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = Color(0xFF17C3CE),
                shape = RoundedCornerShape(size = 17.dp)
            )
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .clickable { onClicked() }
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Sort Options",
            modifier = Modifier
                .clickable {
                    onClicked()
                }
        )
    }
}
