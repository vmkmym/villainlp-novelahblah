package com.example.villainlp.novel.library

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.novel.common.AuthorText
import com.example.villainlp.novel.common.Book
import com.example.villainlp.novel.common.DeleteNovelCard
import com.example.villainlp.novel.common.DescriptionText
import com.example.villainlp.novel.common.DropDownBox
import com.example.villainlp.novel.common.FrontArrowImage
import com.example.villainlp.novel.common.SwipeableBox
import com.example.villainlp.novel.common.TitleText
import com.example.villainlp.novel.common.TopBarTitle
import com.example.villainlp.novel.common.deleteContents
import com.example.villainlp.novel.common.formatRating
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.MyScaffold
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
            scope.launch {
                commentCount = FirebaseTools.getCommentCount(book.documentID ?: "ERROR")
            }

            SwipeableBox(
                modifier = Modifier.animateItemPlacement(),
                onConfirmValueChange = {
                    if (isCurrentUser) {
                        deleteContents(it) { viewModel.onDeleteClicked(book) } // it 값이 SwipeToDismissBoxValue로  변경 그에 따른 함수 파라미터 속성값 변경
                    } else {
                        false
                    }
                },
                backgroundContent = { color ->
                    if (isCurrentUser) {
                        DeleteNovelCard(
                            color = color,
                            text = "삭제하기",
                            cardHeight = 133.dp,
                            imageVector = Icons.Default.Delete
                        )
                    } else {
                        DeleteNovelCard(
                            color = Color.Red,
                            text = "타인의 작품은 삭제 할 수 없습니다.",
                            cardHeight = 133.dp,
                            imageVector = Icons.Default.Lock
                        )
                    }
                },
                content = {
                    NovelCard(
                        book = book,
                        commentCount = commentCount,
                        navController = navController,
                        isCurrentUser = isCurrentUser,
                        onClicked = { navController.navigate("Report/${book.userID}/${book.author}") },
                        onDelete = { viewModel.onDeleteClicked(book) }
                    )
                }
            )

            Spacer(modifier = Modifier.size(25.dp))
        }
    }
}

// 내가 올린 소설(삭제를 위함) UI 구성
@Composable
fun NovelCard(
    book: Book,
    commentCount: Int,
    navController: NavHostController,
    isCurrentUser: Boolean,
    onClicked: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(133.dp)
            .clickable { navController.navigate("ReadLibraryBookScreen/${book.title}/${book.script}/${book.documentID}/${book.views}") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            DropDownBox(
                modifier = Modifier.align(Alignment.TopEnd),
                isCurrentUser = isCurrentUser,
                onClicked = { onClicked() },
                onDelete = { onDelete() }
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TitleAndDescription(book)
                AuthorAndRatingIcons(book, commentCount)
            }
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
