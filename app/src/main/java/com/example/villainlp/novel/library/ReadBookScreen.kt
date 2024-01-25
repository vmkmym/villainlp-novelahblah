package com.example.villainlp.novel.library

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import com.example.villainlp.R
import com.example.villainlp.novel.formatRating
import com.example.villainlp.server.FirebaseTools
import kotlinx.coroutines.launch

@Composable
fun ReadLibraryBookScreen(
    navController: NavHostController,
    title: String,
    script: String,
    documentId: String,
) {
    ReadLibraryBookScaffold(title, navController, documentId) { modifier, listState ->
        LazyColumn(
            modifier = modifier,
            state = listState,
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

// ReadBook
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ReadLibraryBookScaffold(
    title: String,
    navController: NavHostController,
    documentId: String,
    content: @Composable (Modifier, LazyListState) -> Unit,
) {
    var barVisible by remember { mutableStateOf(true) }
    // 레이지컬럼에 상태 추적
    val listState = rememberLazyListState()
    var commentCount by remember { mutableStateOf(0) }
    var rating by remember { mutableStateOf(0.0f) }
    val scope = rememberCoroutineScope()

    scope.launch {
        commentCount = FirebaseTools.getCommentCount(documentId)
        rating = formatRating(FirebaseTools.getRatingFromFirestore(documentId)!!)
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = barVisible,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                ReadLibraryBookScaffoldTopBar(title, navController)
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = barVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Column {
                    Divider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .background(color = Color.White)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "별"
                        )
                        Spacer(modifier = Modifier.size(7.dp))
                        Text(
                            text = "$rating",
                            color = Color(0xFFDD2424)
                        )
                        Spacer(modifier = Modifier.size(15.dp))
                        Row(
                            modifier = Modifier.clickable { navController.navigate("CommentScreen/${documentId}") },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.message),
                                contentDescription = "댓글"
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = "$commentCount",
                                style = TextStyle(
                                    color = Color(0xFFAFAFAF),
                                    fontSize = 18.sp
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(65.dp)
                                    .border(1.dp, Color(0xFFBBBBBB), RoundedCornerShape(5.dp))
                                    .padding(5.dp),
                                onClick = { navController.navigate("RatingScreen/${documentId}") },
                                colors = ButtonDefaults.primaryButtonColors(
                                    backgroundColor = Color.Transparent
                                ),
                                shape = RectangleShape
                            ) {
                                Text(
                                    text = "별점주기",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF000000)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        barVisible = !barVisible
                    })
                },
            listState
        )
    }
}

@Composable
fun ReadLibraryBookScaffoldTopBar(
    title: String,
    navController: NavHostController,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(20.dp),
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "back"
            )
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF212121),
                )
            )
            Spacer(modifier = Modifier.size(1.dp))
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}