package com.example.villainlp.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.Book
import com.example.villainlp.model.FirebaseTools
import com.example.villainlp.model.FirebaseTools.updateBookViews
import com.example.villainlp.model.NovelInfo
import com.example.villainlp.model.RelayChatToNovelBook
import com.example.villainlp.model.Screen
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                .background(
                    color = Color(0xFFFFFFFF)
                ),
        )
    }
}


@Composable
fun MyScaffoldBottomBar(navController: NavHostController) {
    val currentScreen = remember { mutableStateOf(navController.currentDestination?.route) }
    Column {
        Divider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
        Row(
            Modifier
                .width(428.dp)
                .height(80.dp)
                .background(color = Color(0xFFFFFFFF), RoundedCornerShape(17.dp))
                .padding(start = 33.dp, top = 16.dp, end = 33.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            // Child views.
            CustomIconButton(
                defaultIcon = R.drawable.home,
                clickedIcon = R.drawable.home_clicked,
                isCurrentScreen = currentScreen.value == Screen.CreativeYard.route,
                iconText = "창작마당",
                clickedTextColor = Blue789,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.CreativeYard.route) }
            CustomIconButton(
                defaultIcon = R.drawable.forum,
                clickedIcon = R.drawable.forum_clicked,
                isCurrentScreen = currentScreen.value == Screen.ChattingList.route,
                iconText = "릴레이소설",
                clickedTextColor = Blue789,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.ChattingList.route) }
            CustomIconButton(
                defaultIcon = R.drawable.book_5,
                clickedIcon = R.drawable.book_clicked,
                isCurrentScreen = currentScreen.value == Screen.MyBook.route,
                iconText = "내서재",
                clickedTextColor = Blue789,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.MyBook.route) }
            CustomIconButton(
                defaultIcon = R.drawable.local_library,
                clickedIcon = R.drawable.local_library_clicked,
                isCurrentScreen = currentScreen.value == Screen.Library.route,
                iconText = "도서관",
                clickedTextColor = Blue789,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.Library.route) }
            CustomIconButton(
                defaultIcon = R.drawable.settings,
                clickedIcon = R.drawable.settings_clicked,
                isCurrentScreen = currentScreen.value == Screen.Profile.route,
                iconText = "설정",
                clickedTextColor = Blue789,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.Profile.route) }
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
                    fontWeight = FontWeight(600),
                    color = Color(0xFF212121),
                )
            )
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}


@Composable
fun CustomIconButton(
    defaultIcon: Int,
    clickedIcon: Int,
    isCurrentScreen: Boolean,
    iconText: String,
    clickedTextColor: Color,
    defaultTextColor: Color,
    onClicked: () -> Unit,
) {
    val icon = if (isCurrentScreen) clickedIcon else defaultIcon
    val textColor = if (isCurrentScreen) clickedTextColor else defaultTextColor

    Column(
        modifier = Modifier.clickable { onClicked() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(0.dp)
                .width(28.dp)
                .height(28.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .height(17.dp),
            text = iconText,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight(500),
                color = textColor,
            )
        )
    }
}

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
                        updateBookViews(book.documentID ?: "ERROR", viewCount)
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
                    updateBookViews(book.documentID ?: "ERROR", viewCount)
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


@Composable
fun ReadMyBookScaffold(
    title: String,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
    onClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            ReadMyBookScaffoldTopBar(title, navController) { onClicked() }
        },
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

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
fun ReadMyBookScaffoldTopBar(
    title: String,
    navController: NavHostController,
    onClicked: () -> Unit,
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
                    fontWeight = FontWeight(500),
                    color = Color(0xFF212121),
                )
            )
            Image(
                modifier = Modifier.clickable { onClicked() },
                painter = painterResource(id = R.drawable.file_upload),
                contentDescription = "upload"
            )
        }
        Divider(color = Color(0xFF9E9E9E))
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


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowChats(
    modifier: Modifier,
    navController: NavHostController,
    novelInfo: List<NovelInfo>,
    onClicked: (NovelInfo) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(novelInfo) { novelInfo ->
            NovelChatCards(novelInfo, navController) { selectedChatting ->
                onClicked(selectedChatting)
            }
            Spacer(modifier = Modifier.size(15.dp))
        }
        item { AddChatCard(navController) }
    }
}

@Composable
fun AddChatCard(navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(378.dp)
            .height(100.dp)
            .border(
                color = Color(0xFFF5F5F5),
                width = 2.dp,
                shape = RoundedCornerShape(size = 19.dp),
            )
            .clickable { navController.navigate(Screen.CreativeYard.route) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.maps_ugc),
                contentDescription = "add chat"
            )
        }
    }

}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NovelChatCards(
    novelInfo: NovelInfo,
    navController: NavHostController,
    onClicked: (NovelInfo) -> Unit,
) {
    val context = LocalContext.current

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
                .background(Color.White)
                .width(370.dp)
                .height(100.dp)
                .offset {
                    IntOffset(
                        swipeableState.offset.value.roundToInt(),
                        0
                    )
                }
                .then(swipeableModifier)
                .clickable { navController.navigate("ChattingScreen/${novelInfo.title}/${novelInfo.threadId}/${novelInfo.assistId}") },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(
                            id = if (novelInfo.assistId == getString(
                                    context,
                                    R.string.assistant_key_for_novelist
                                )
                            )
                                R.drawable.creative_yard_1 else R.drawable.creative_yard_2
                        ),
                        contentDescription = "Working On"
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .width(200.dp)
                                .height(22.dp),
                            text = novelInfo.title,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight(600),
                                color = Color(0xFF212121),
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            modifier = Modifier
                                .width(222.dp)
                                .height(30.dp),
                            text = if (novelInfo.assistId == getString(
                                    context,
                                    R.string.assistant_key_for_novelist
                                )
                            ) "작가의 마당에서 작업중..." else "꿈의 마당에서 작업중...",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFFBBBBBB),
                            )
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
            }
        }
        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        800,
                        0
                    )
                } // Use the same offset as the Card
                .size(65.dp, 100.dp)
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
                        .clickable { onClicked(novelInfo) },
                    composition = fireLottie,
                    iterations = LottieConstants.IterateForever
                )
            }
        }

    }
}

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


fun formatRating(rating: Float): Float {
    val formattedString = String.format("%.2f", rating)
    return formattedString.toFloat()
}