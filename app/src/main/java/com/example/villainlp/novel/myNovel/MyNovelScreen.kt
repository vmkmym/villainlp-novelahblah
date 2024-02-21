package com.example.villainlp.novel.myNovel

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.novel.AuthorText
import com.example.villainlp.novel.DescriptionText
import com.example.villainlp.novel.FrontArrowImage
import com.example.villainlp.novel.RelayChatToNovelBook
import com.example.villainlp.novel.TitleText
import com.example.villainlp.novel.TopBarTitle
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyBookScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: MyNovelViewModel = viewModel(),
) {
    val novelList by viewModel.novelList.collectAsState()

    viewModel.loadNovels(auth)

    MyScaffold(TopBarTitle.MyNovel.title, navController) {
        MyNovels(it, navController, auth, novelList, viewModel)
    }
}

// 내 소설들을 모아둔 Column
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MyNovels(
    modifier: Modifier,
    navController: NavHostController,
    auth: FirebaseAuth,
    myBooks: List<RelayChatToNovelBook>, // TODO : myBooks와 viewModel이 RelayChatToNovelBook과 MyNovelViewModel로 고정되어 있는데
    viewModel: MyNovelViewModel,         // TODO : 이를 통합되게 관리하면 이 코드를 비슷한 부분에서 다같이 사용할 수 있음(재사용성 증진)
) {
    LazyColumn(
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(myBooks, key = { item -> item.documentID ?: "ERROR" }) { book ->

            val dismissState = rememberDismissState(
                positionalThreshold = { it * 0.50f },
                confirmValueChange = { deleteNovel(it){ viewModel.onDeleteClicked(book, auth) } }
            )

            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == DismissValue.DismissedToStart) Color.Red else Blue789,
                label = "ColorAnimation"
            )

            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = { DeleteNovelCard(color) },
                dismissContent = { NovelCard(navController, book) }
            )

            Spacer(modifier = Modifier.fillMaxWidth().size(15.dp))
        }
    }
}

// 스와이프시 삭제(Library, MyNovel)
@OptIn(ExperimentalMaterial3Api::class)
fun deleteNovel(
    it: DismissValue,
    onConfirmValueChanged: () -> Unit
): Boolean {
    if (it == DismissValue.DismissedToStart) {
        CoroutineScope(Dispatchers.Main).launch {
            onConfirmValueChanged()
            delay(200)
        }
    }
    return true
}

// 스와이프 했을때 나오는 배경
@Composable
private fun DeleteNovelCard(color: Color) {
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
                    text = "삭제하기",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White,
                    ),
                )
                Icon(
                    modifier = Modifier.padding(end = 15.dp),
                    imageVector = Icons.Default.Delete,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun NovelCard(
    navController: NavHostController,
    book: RelayChatToNovelBook,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // LazyColumn의 넓이에 맞춤
            .height(133.dp)
            .clickable { navController.navigate("ReadMyBookScreen/${book.title}/${book.script}") },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp), // Content가 너무 딱 붙어서 패딩을 줌
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween, // 요소들을 가로 방향으로 공간을 균등하게 배치
        ) {
            Column(modifier = Modifier.weight(1f)) { // 왼쪽에 배치되는 열을 확장하여 오른쪽에 빈 공간을 만듦, 화살표가 보이게 하기위해
                NovelTitleAndScript(book)
                NovelAuthor(book)
            }
            FrontArrowImage() // 오른쪽에 배치
        }
    }
}

// 소설 제목, 내용 부분의 Row
@Composable
fun NovelTitleAndScript(book: RelayChatToNovelBook) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            TitleText(book.title)
            Spacer(modifier = Modifier.padding(top = 2.dp))
            DescriptionText(book.script)
        }
    }
}

// 소설 저자를 나타내는 Row
@Composable
fun NovelAuthor(book: RelayChatToNovelBook) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    )
    {
        AuthorText(book.author)
    }
}
