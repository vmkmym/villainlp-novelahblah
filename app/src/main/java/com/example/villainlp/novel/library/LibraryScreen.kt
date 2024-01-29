package com.example.villainlp.novel.library

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.novel.AlertPopup
import com.example.villainlp.novel.Book
import com.example.villainlp.novel.DeleteAlert
import com.example.villainlp.novel.FrontArrowImage
import com.example.villainlp.novel.TopBarTitle
import com.example.villainlp.novel.createSwipeableParameters
import com.example.villainlp.novel.formatRating
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.MyScaffold
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: LibraryViewModel = viewModel(),
) {
    val novelList by viewModel.novelList.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()

    MyScaffold(TopBarTitle.Library.title, navController) {
        Column(
            modifier = it.padding(12.dp)
        ) {
            SortButtons(viewModel)
            NovelLists(navController, novelList, auth) { selectedNovel -> viewModel.onDeleteClicked(selectedNovel) }
        }
    }
    if (showDialog) {
        AlertPopup(
            title = DeleteAlert.CommonTitle.text,
            message = DeleteAlert.LibraryMessage.text,
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked() }
        )
    }
}

// 정렬 아이콘 버튼과 그에 따른 Ui상태
@Composable
fun SortButtons(
    viewModel: LibraryViewModel
){
    viewModel.loadNovels() // loadNovels를 안넣어주면 상태 업데이트를 못함

    val isRateClicked by viewModel.isRateClicked.collectAsState()
    val isViewClicked by viewModel.isViewClicked.collectAsState()
    val isUpdateClicked by viewModel.isUpdateClicked.collectAsState()

    val starIcon = if(isRateClicked) R.drawable.star_white else R.drawable.star_sky
    val viewIcon = if(isViewClicked) R.drawable.views_white else R.drawable.views
    val updateIcon = if(isUpdateClicked) R.drawable.clock_white else R.drawable.clock

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
}

// 정렬 버튼 UI
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

// 올라온 소설 목록들
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NovelLists(
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
            NovelCardBox(book, navController, auth) { selectedBook -> onClicked(selectedBook) }
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

// 각각의 소설박스 UI 구성
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NovelCardBox(
    book: Book,
    navController: NavHostController,
    auth: FirebaseAuth,
    onClicked: (Book) -> Unit,
) {
    var commentCount by remember { mutableStateOf(0) }
    val user = auth.currentUser
    val isCurrentUser = user?.uid == book.userID
    val scope = rememberCoroutineScope()

    scope.launch { commentCount = FirebaseTools.getCommentCount(book.documentID!!) }

    val (swipeableState, swipeableModifier, imageVisibility) = createSwipeableParameters()

    if (isCurrentUser) {
        Box {
            MyNovelCard(
                book = book,
                commentCount = commentCount,
                swipeableModifier = swipeableModifier,
                offset = { IntOffset(swipeableState.offset.value.roundToInt(), 0) },
                onClicked = { navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}/${book.views}") }
            )
            DeleteAnimation(
                imageVisibility = imageVisibility,
                book = book,
                onClicked = { onClicked(book) }
            )
        }
    } else {
        AllNovelCard(
            book = book,
            commentCount = commentCount,
            onClicked = { navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}/${book.views}") }
        )
    }
}

// 내가 올린 소설(삭제를 위함) UI 구성
@Composable
fun MyNovelCard(
    book: Book,
    commentCount: Int,
    swipeableModifier: Modifier,
    offset: () -> IntOffset,
    onClicked: () -> Unit
){
    Card(
        modifier = Modifier
            .width(360.dp)
            .height(133.dp)
            .offset { offset() } // Apply the offset
            .then(swipeableModifier) // Apply the swipeable modifier
            .clickable { onClicked() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TitleAndDescription(book)
            AuthorAndRatingIcons(book, commentCount)
        }
    }
}

// 제목, 요약 구성
@Composable
fun TitleAndDescription(book: Book){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleText(book.title)
            DescriptionText(book.description)
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            FrontArrowImage()
        }
    }
}

// 제목 Text 속성
@Composable
fun TitleText(title: String){
    Text(
        modifier = Modifier
            .width(270.dp)
            .height(30.dp),
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight(600),
            color = Color(0xFF212121),
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// 요약 Text 속성
@Composable
fun DescriptionText(description: String){
    Text(
        modifier = Modifier
            .width(270.dp)
            .height(45.dp),
        text = description,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF2C2C2C),
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

// 저자, 별점, 조회수, 코맨트 수 UI 구성
@Composable
fun AuthorAndRatingIcons(
    book: Book,
    commentCount: Int
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AuthorText(book.author)
        RatingIcons(book, commentCount)
    }
}

// 저자 Text 속성
@Composable
fun AuthorText(author: String){
    Text(
        text = author,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Start,
        )
    )
}

// 별점, 조회수, 코멘트 아이콘 구성
@Composable
fun RatingIcons(
    book: Book,
    commentCount: Int
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RatingCounts(ratingImg = R.drawable.star_sky, count = "${formatRating(book.rating)}")
        Spacer(modifier = Modifier.size(9.dp))

        RatingCounts(ratingImg = R.drawable.views_black, count = "${book.views}")
        Spacer(modifier = Modifier.size(9.dp))

        RatingCounts(ratingImg = R.drawable.message, count = "$commentCount")
    }
}

// 아이콘과 글씨 묶음 속성
@Composable
fun RatingCounts(
    ratingImg: Int,
    count: String
){
    Image(
        modifier = Modifier.size(15.dp),
        painter = painterResource(id = ratingImg),
        contentDescription = "stars"
    )
    Spacer(modifier = Modifier.size(2.dp))
    Text(
        text = count,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Start,
        )
    )
}

// 삭제 애니메이션 구성
@Composable
fun DeleteAnimation(
    imageVisibility: Boolean,
    book: Book,
    onClicked: (Book) -> Unit
){
    val fireLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_red))

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

// 다른 사람이 올린 소설(지울수 없음) 구성
@Composable
fun AllNovelCard(
    book: Book,
    commentCount: Int,
    onClicked: () -> Unit
){
    Card(
        modifier = Modifier
            .width(360.dp)
            .height(133.dp)
            .clickable { onClicked() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TitleAndDescription(book)
            AuthorAndRatingIcons(book, commentCount)
        }
    }
}