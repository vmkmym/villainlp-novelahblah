package com.example.villainlp.novel.library

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.villainlp.novel.Book
import com.example.villainlp.novel.formatRating
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.MyScaffoldBottomBar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: LibraryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val novelList by viewModel.novelList.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val isRateClicked by viewModel.isRateClicked.collectAsState()
    val isViewClicked by viewModel.isViewClicked.collectAsState()
    val isUpdateClicked by viewModel.isUpdateClicked.collectAsState()

    val starIcon = if(isRateClicked) R.drawable.star_white else R.drawable.star_sky
    val viewIcon = if(isViewClicked) R.drawable.views_white else R.drawable.views
    val updateIcon = if(isUpdateClicked) R.drawable.clock_white else R.drawable.clock

    // 정렬이 바뀔때마다 Novels를 로드하는 방식이 달라짐
    viewModel.loadNovels()

    MyLibraryScaffold(
        "도서관", navController
    ) {
        Column(
            modifier = it.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortOptionButton(
                    image = starIcon,
                    backColor = if (isRateClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.rateClicked() }
                )
                SortOptionButton(
                    image = viewIcon,
                    backColor = if (isViewClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.viewClicked() }
                )
                SortOptionButton(
                    image = updateIcon,
                    backColor = if (isUpdateClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.updateClicked() }
                )
            }
            ShowAllBooks(navController, novelList, auth) { selectedNovel -> viewModel.onDeleteClicked(selectedNovel) }
        }
    }

    if (showDialog) {
        AlertPopup(
            title = "정말로 삭제하시겠습니까?",
            message = "선택한 소설이 삭제됩니다.",
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked() }
        )
    }
}


@Composable
fun SortOptionButton(
    image: Int,
    backColor: Color,
    onClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = Color(0xFF17C3CE),
                shape = RoundedCornerShape(size = 17.dp)
            )
            .background(
                color = backColor,
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
        )
    }
}

// LibraryScreen
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowAllBooks(
    navController: NavHostController,
    books: List<Book>,
    auth: FirebaseAuth,
    onClicked: (Book) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(books) { book ->
            LibraryBookCards(book, navController, auth) { selectedBook -> onClicked(selectedBook) }
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun LibraryBookCards(
    book: Book,
    navController: NavHostController,
    auth: FirebaseAuth,
    onClicked: (Book) -> Unit,
) {
    var viewCount by remember { mutableStateOf(book.views) }
    var commentCount by remember { mutableStateOf(0) }
    val user = auth.currentUser
    val isCurrentUser = user?.uid == book.userID
    val scope = rememberCoroutineScope()

    scope.launch {
        commentCount = FirebaseTools.getCommentCount(book.documentID!!)
    }

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

    if (isCurrentUser) {
        Box {
            Card(
                modifier = Modifier
//                .border(2.dp, color = Blue789)
                    .width(360.dp)
                    .height(133.dp)
                    .offset {
                        IntOffset(
                            swipeableState.offset.value.roundToInt(),
                            0
                        )
                    } // Apply the offset
                    .then(swipeableModifier) // Apply the swipeable modifier
                    .clickable {
                        viewCount += 1
                        FirebaseTools.updateBookViews(book.documentID ?: "ERROR", viewCount)
                        navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}")
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
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
                            Text(
                                modifier = Modifier
                                    .width(270.dp)
                                    .height(45.dp),
                                text = book.description,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF2C2C2C),
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(33.dp)
                                    .padding(5.dp),
                                painter = painterResource(id = R.drawable.arrow_right),
                                contentDescription = "Front Arrow"
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = book.author,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Start,
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Image(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(id = R.drawable.star_sky),
                                contentDescription = "stars"
                            )
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(
                                text = "${formatRating(book.rating)}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Start,
                                )
                            )
                            Spacer(modifier = Modifier.size(9.dp))
                            Image(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(id = R.drawable.views_black),
                                contentDescription = "views"
                            )
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(
                                text = "${book.views}",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Start,
                                )
                            )
                            Spacer(modifier = Modifier.size(9.dp))
                            Image(
                                modifier = Modifier.size(15.dp),
                                painter = painterResource(id = R.drawable.message),
                                contentDescription = "댓글"
                            )
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(
                                text = "$commentCount",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Start,
                                )
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .size(360.dp, 133.dp)
                    .border(
                        1.dp, Color(0xFFF5F5F5),
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .padding(12.dp),
                horizontalAlignment = Alignment.End,
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
    } else {
        Card(
            modifier = Modifier
//                .border(2.dp, color = Blue789)
                .width(360.dp)
                .height(133.dp)
                .clickable {
                    viewCount += 1
                    FirebaseTools.updateBookViews(book.documentID ?: "ERROR", viewCount)
                    navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}")
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
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
                        Text(
                            modifier = Modifier
                                .width(270.dp)
                                .height(45.dp),
                            text = book.description,
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
                ) {
                    Text(
                        text = book.author,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Start,
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(id = R.drawable.star_sky),
                            contentDescription = "stars"
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = "${formatRating(book.rating)}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Start,
                            )
                        )
                        Spacer(modifier = Modifier.size(9.dp))
                        Image(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(id = R.drawable.views_black),
                            contentDescription = "views"
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = "${book.views}",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Start,
                            )
                        )
                        Spacer(modifier = Modifier.size(9.dp))
                        Image(
                            modifier = Modifier.size(15.dp),
                            painter = painterResource(id = R.drawable.message),
                            contentDescription = "댓글"
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(
                            text = "$commentCount",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Start,
                            )
                        )
                    }
                }
            }
        }
    }
}

// LibraryScreen
@Composable
fun MyLibraryScaffold(
    title: String,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            MyLibraryScaffoldTopBar(title)
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
fun MyLibraryScaffoldTopBar(title: String) {
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
                    fontWeight = FontWeight(600),
                    color = Color(0xFF212121),
                )
            )
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}