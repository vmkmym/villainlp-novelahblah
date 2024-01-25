package com.example.villainlp.novel.myNovel

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.novel.AlertPopup
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.shared.MyScaffold
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyBookScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: MyNovelViewModel = viewModel(),
) {
    val showDialog by viewModel.showDialog.collectAsState()
    val novelList by viewModel.novelList.collectAsState()

    viewModel.loadNovels(auth)

    MyScaffold("내서재", navController) {
        ShowMyBooks(it, navController, novelList) { selectedNovel ->
            viewModel.onDeleteClicked(selectedNovel)
        }

        if (showDialog) {
            AlertPopup(
                title = "정말로 삭제하시겠습니까?",
                message = "내 작업 공간에서 선택한 소설이 삭제가 됩니다.",
                onDismiss = { viewModel.onDismissDialog() },
                onConfirm = { viewModel.onConfirmClicked(auth) }
            )
        }
    }
}

// MyNovelScreen
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowMyBooks(
    modifier: Modifier,
    navController: NavHostController,
    myBooks: List<RelayChatToNovelBook>,
    onClicked: (RelayChatToNovelBook) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(myBooks) { book ->
            MyBookCards(book, navController) { selectedBook -> onClicked(selectedBook) }
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun MyBookCards(
    book: RelayChatToNovelBook,
    navController: NavHostController,
    onClicked: (RelayChatToNovelBook) -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0f)

    val swipeableModifier = Modifier.swipeable(
        state = swipeableState,
        anchors = mapOf(0f to 0f, -150f to -150f), // Define the anchors
        orientation = Orientation.Horizontal,
        thresholds = { _, _ -> FractionalThreshold(0.1f) },
        resistance = null
    )

    // Define the AnimatedVisibility for the image
    val imageVisibility = swipeableState.offset.value <= -150f

    val fireLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_red))

    Box {
        Card(
            modifier = Modifier
                .width(360.dp)
                .height(133.dp)
                .offset {
                    IntOffset(
                        swipeableState.offset.value.roundToInt(),
                        0
                    )
                } // Apply the offset
                .then(swipeableModifier) // Apply the swipeable modifier
                .clickable { navController.navigate("ReadMyBookScreen/${book.title}/${book.script}") },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .width(270.dp)
                                .height(30.dp),
                            text = book.title,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF212121),
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.padding(top = 2.dp))
                        Text(
                            modifier = Modifier
                                .width(270.dp)
                                .height(45.dp),
                            text = book.script,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF2C2C2C),
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Image(
                        modifier = Modifier
                            .size(33.dp)
                            .padding(5.dp),
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = "Front Arrow"
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .height(16.dp),
                        text = book.author,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
        }
        // The image that appears when swiped
        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        775,
                        0
                    )
                } // Use the same offset as the Card
                .size(65.dp, 133.dp)
                .border(
                    1.dp, Color(0xFFF5F5F5),
                    RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = imageVisibility,
                enter = scaleIn(),
                exit = ExitTransition.None
            ) {
                LottieAnimation(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onClicked(book) },
                    composition = fireLottie,
                    iterations = LottieConstants.IterateForever
                )
            }
        }
    }
}