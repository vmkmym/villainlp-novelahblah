package com.example.villainlp.chat.openAichat

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.GenNovelViewModelFactory
import com.example.villainlp.R
import com.example.villainlp.novel.common.DeleteNovelCard
import com.example.villainlp.novel.common.FrontArrowImage
import com.example.villainlp.novel.common.SwipeableBox
import com.example.villainlp.novel.common.deleteContents
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.shared.NovelInfo
import com.example.villainlp.shared.Screen
import com.example.villainlp.ui.theme.BasicWhite
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatListScreen(navController: NavHostController, auth: FirebaseAuth) {
    val chatListViewModel: ChatListViewModel =
        viewModel(factory = GenNovelViewModelFactory(auth, ChatModel()))
    val novelInfo by chatListViewModel.novelInfo.collectAsState()

    MyScaffold("내 작업 공간", navController) {
        ShowChats(it, navController, novelInfo, chatListViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ShowChats(
    modifier: Modifier,
    navController: NavHostController,
    novelInfo: List<NovelInfo>,
    chatListViewModel: ChatListViewModel,
) {
    LazyColumn(
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(novelInfo, key = { item -> item.documentID ?: "ERROR" }) { novelInfo ->

            SwipeableBox(
                modifier = Modifier.animateItemPlacement(),
                onConfirmValueChange = {
                    deleteContents(it) {
                        chatListViewModel.deleteChatting(
                            novelInfo
                        )
                    }
                },
                backgroundContent = { color ->
                    DeleteNovelCard(
                        color = color,
                        text = "삭제하기",
                        cardHeight = 133.dp,
                        imageVector = Icons.Default.Delete
                    )
                },
                content = { NovelChatCards(novelInfo, navController) }
            )

            Spacer(modifier = Modifier.size(15.dp))
        }

        item {
            AddChatCard(navController)
        }
    }
}

@Composable
fun AddChatCard(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(
                color = Color(0xFFF5F5F5),
                width = 1.dp,
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

@Composable
fun NovelChatCards(
    novelInfo: NovelInfo,
    navController: NavHostController,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                if (novelInfo.threadId == "" || novelInfo.assistId == "") {
                    navController.navigate("GeminiChatScreen/${novelInfo.title}/${novelInfo.uuid}")
                } else {
                    navController.navigate("ChattingScreen/${novelInfo.title}/${novelInfo.threadId}/${novelInfo.assistId}")
                }
            },
        colors = CardDefaults.cardColors(containerColor = BasicWhite),
    ) {

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // 화살표 정렬을 위해 추가
        ) {
            Image(
                painter = painterResource(
                    id = if (novelInfo.assistId == ContextCompat.getString(
                            context,
                            R.string.assistant_key_for_novelist
                        )
                    )
                        R.drawable.creative_yard_1 else R.drawable.creative_yard_2
                ),
                contentDescription = "Working On"
            )
            Spacer(modifier = Modifier.padding(7.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.wrapContentWidth(), // 반응형
                    text = novelInfo.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.padding(7.dp))
                Text(
                    modifier = Modifier.wrapContentWidth(), // 반응형
                    text = if (novelInfo.assistId == ContextCompat.getString(
                            context,
                            R.string.assistant_key_for_novelist
                        )
                    ) "작가의 마당에서 작업 중..." else "꿈의 마당에서 작업 중...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = DarkGray,
                )
            }
            FrontArrowImage()
        }
    }
}
