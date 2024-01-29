package com.example.villainlp.novel.myNovel

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.villainlp.novel.DeleteAlert
import com.example.villainlp.novel.FrontArrowImage
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.novel.TopBarTitle
import com.example.villainlp.novel.createSwipeableParameters
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

    MyScaffold(TopBarTitle.MyNovel.title, navController) {
        MyNovels(it, navController, novelList) { selectedNovel ->
            viewModel.onDeleteClicked(selectedNovel)
        }
        if (showDialog) {
            AlertPopup(
                title = DeleteAlert.CommonTitle.text,
                message = DeleteAlert.MyNovelMessage.text,
                onDismiss = { viewModel.onDismissDialog() },
                onConfirm = { viewModel.onConfirmClicked(auth) }
            )
        }
    }
}

// 내 소설들을 모아둔 Column
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyNovels(
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
            NovelCardBox(book, navController) { selectedBook -> onClicked(selectedBook) }
            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

// 각각의 소설을 한눈에 보여주는 Card
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun NovelCardBox(
    book: RelayChatToNovelBook,
    navController: NavHostController,
    onClicked: (RelayChatToNovelBook) -> Unit,
) {
    val (swipeableState, swipeableModifier, imageVisibility) = createSwipeableParameters()

    Box {
        NovelCard(
            offset = { IntOffset(swipeableState.offset.value.roundToInt(), 0) },
            onClick = { navController.navigate("ReadMyBookScreen/${book.title}/${book.script}") },
            swipeableModifier = swipeableModifier,
            book = book
        )
        DeleteAnimation(
            imageVisibility = imageVisibility,
            book = book ,
            onClicked = { onClicked(book) } // TODO : 여기서 swipeableState값을 0으로 조절하면 될듯?
        )
    }
}

// 소설 Card
@Composable
fun NovelCard(
    offset: () -> IntOffset,
    onClick: () -> Unit,
    swipeableModifier: Modifier,
    book: RelayChatToNovelBook
){
    Card(
        modifier = Modifier
            .width(360.dp)
            .height(133.dp)
            .offset { offset() } // Apply the offset
            .then(swipeableModifier) // Apply the swipeable modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            NovelTitleAndScript(book)
            NovelAuthor(book)
        }
    }
}

// 소설 제목, 내용 부분의 Row
@Composable
fun NovelTitleAndScript(book: RelayChatToNovelBook){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NovelTitleText(book.title)
            Spacer(modifier = Modifier.padding(top = 2.dp))
            NovelScriptText(book.script)
        }
        FrontArrowImage()
    }
}

// 소설 제목 Text 함수
@Composable
fun NovelTitleText(
    title: String
){
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

// 소설 내용 Text 함수
@Composable
fun NovelScriptText(
    script: String
){
    Text(
        modifier = Modifier
            .width(270.dp)
            .height(45.dp),
        text = script,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF2C2C2C),
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}



// 소설 저자 Text 함수
@Composable
fun NovelAuthorText(
    author: String
){
    Text(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .height(16.dp),
        text = author,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Start
        )
    )
}

// 소설 저자를 나타내는 Row
@Composable
fun NovelAuthor(book: RelayChatToNovelBook){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        NovelAuthorText(book.author)
    }
}

// 소설 Card를 Swipe 했을 때 나오는 로티 애니메이션 Column
@Composable
fun DeleteAnimation(
    imageVisibility: Boolean,
    book: RelayChatToNovelBook,
    onClicked: (RelayChatToNovelBook) -> Unit,
    ){
    val fireLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_red))

    Column(
        modifier = Modifier
            .offset { IntOffset(775, 0) } // Use the same offset as the Card
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

