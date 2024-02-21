package com.example.villainlp.novel.library

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.novel.AuthorText
import com.example.villainlp.novel.Book
import com.example.villainlp.novel.DescriptionText
import com.example.villainlp.novel.FrontArrowImage
import com.example.villainlp.novel.TitleText
import com.example.villainlp.novel.TopBarTitle
import com.example.villainlp.novel.formatRating
import com.example.villainlp.novel.myNovel.deleteNovel
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: LibraryViewModel = viewModel(),
) {
    val novelList by viewModel.novelList.collectAsState()

    MyScaffold(TopBarTitle.Library.title, navController) {
        Column(
            modifier = it.padding(12.dp)
        ) {
            SortButtons(viewModel)
            NovelLists(navController, novelList, auth, viewModel)
        }
    }
}

// 정렬 아이콘 버튼과 그에 따른 Ui상태
@Composable
fun SortButtons(
    viewModel: LibraryViewModel,
) {
    viewModel.loadNovels() // loadNovels를 안넣어주면 상태 업데이트를 못함

    val isRateClicked by viewModel.isRateClicked.collectAsState()
    val isViewClicked by viewModel.isViewClicked.collectAsState()
    val isUpdateClicked by viewModel.isUpdateClicked.collectAsState()

    val starIcon = if (isRateClicked) R.drawable.star_white else R.drawable.star_sky
    val viewIcon = if (isViewClicked) R.drawable.views_white else R.drawable.views
    val updateIcon = if (isUpdateClicked) R.drawable.clock_white else R.drawable.clock

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
    onClicked: () -> Unit,
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NovelLists(
    navController: NavHostController,
    books: List<Book>,
    auth: FirebaseAuth,
    viewModel: LibraryViewModel,
) {
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(books, key = { item -> item.documentID ?: "ERROR" }) { book ->

            val user = auth.currentUser
            val isCurrentUser = user?.uid == book.userID
            var commentCount by remember { mutableStateOf(0) }

            val dismissState = rememberDismissState(
                positionalThreshold = { it * 0.50f },
                confirmValueChange = {
                    if (isCurrentUser) {
                        deleteNovel(it) { viewModel.onDeleteClicked(book) }
                    } else {
                        false
                    }
                }
            )

            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) Color.Red else Blue789,
                label = "ColorAnimation"
            )

            scope.launch { commentCount = FirebaseTools.getCommentCount(book.documentID?:"ERROR") }

            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    if (isCurrentUser) {
                        DeleteNovelCard(
                            color = color,
                            text = "삭제하기",
                            imageVector = Icons.Default.Delete
                        )
                    } else {
                        DeleteNovelCard(
                            color = Color.Red,
                            text = "타인의 작품은 삭제 할 수 없습니다.",
                            imageVector = Icons.Default.Lock
                        )
                    }
                },
                dismissContent = {
                    NovelCard(
                        book = book,
                        commentCount = commentCount,
                        navController = navController
                    )
                }
            )

            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

// 스와이프 삭제(Library, MyNovel)
@Composable
private fun DeleteNovelCard(color: Color, text: String, imageVector: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // LazyColumn의 넓이에 맞춤
            .height(133.dp),
        colors = CardDefaults.cardColors(containerColor = color), // 50%넘게 스와이프하면 색이 바뀜
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), // Card 크기에 맞춤
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 15.dp),
                    text = text,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White,
                    ),
                )
                Icon(
                    modifier = Modifier.padding(end = 15.dp),
                    imageVector = imageVector,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    }
}

// 내가 올린 소설(삭제를 위함) UI 구성
@Composable
fun NovelCard(
    book: Book,
    commentCount: Int,
    navController: NavHostController,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(133.dp)
            .clickable { navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}/${book.views}") },
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
fun TitleAndDescription(book: Book) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            TitleText(book.title)
            Spacer(modifier = Modifier.padding(top = 2.dp))
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

// 저자, 별점, 조회수, 코맨트 수 UI 구성
@Composable
fun AuthorAndRatingIcons(
    book: Book,
    commentCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AuthorText(book.author)
        RatingIcons(book, commentCount)
    }
}

// 별점, 조회수, 코멘트 아이콘 구성
@Composable
fun RatingIcons(
    book: Book,
    commentCount: Int,
) {
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
    count: String,
) {
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
