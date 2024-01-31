package com.example.villainlp.chat.openAichat

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import com.example.villainlp.GenNovelViewModelFactory
import com.example.villainlp.R
import com.example.villainlp.novel.NovelInfo
import com.example.villainlp.ui.theme.Blue789
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.shared.Screen
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChatListScreen(navController: NavHostController, auth: FirebaseAuth) {
    val chatListViewModel: ChatListViewModel =
        viewModel(factory = GenNovelViewModelFactory(auth, ChatModel()))

    val documentID by chatListViewModel.documentID.collectAsState()
    val novelInfo by chatListViewModel.novelInfo.collectAsState()

    MyScaffold("내 작업 공간", navController) {
        ShowChats(it, navController, novelInfo) { selectedChatting ->
            chatListViewModel.showDialog(selectedChatting.documentID ?: "ERROR")
        }
    }
    ShowAlertDialog(chatListViewModel)
}

@Composable
fun ShowAlertDialog(chatListViewModel: ChatListViewModel) {
    val showDialog by chatListViewModel.showDialog.collectAsState()
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))

    if (showDialog) {
        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = firePuppleLottie,
                    iterations = LottieConstants.IterateForever
                )
            },
            containerColor = Color.White,
            onDismissRequest = { chatListViewModel.hideDialog() },
            title = {
                Text(
                    text = "정말로 삭제하시겠습니까?",
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "내 작업 공간에서 선택한 소설이 삭제가 됩니다.",
                        style = TextStyle(
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        // Firestore에서 채팅 삭제 후 채팅 목록을 다시 불러옴
                        chatListViewModel.deleteChattingAndFetchChatList()
                    }
                ) {
                    Text(
                        text = "확인",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Blue789
                        )
                    )
                }
            },
            dismissButton = {
                IconButton(
                    onClick = { chatListViewModel.hideDialog() }
                ) {
                    Text(
                        text = "취소",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Blue789
                        )
                    )
                }
            },
            modifier = Modifier.padding(16.dp)
        )
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
        modifier = modifier.padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        items(novelInfo) { novelInfo ->
            NovelChatCards(novelInfo, navController, onClicked)
            Spacer(modifier = Modifier.size(15.dp))
        }
        item {
            AddChatCard(navController)
        }
    }
}


// TODO: AddChatCard로 이동해서 글을 작성하면 UI의 상태가 바로 업데이트가 안됨!!
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
                .clickable {
                    if (novelInfo.threadId == "" || novelInfo.assistId == "") {
                        navController.navigate("GeminiChatScreen/${novelInfo.title}")
                    } else {
                        navController.navigate("ChattingScreen/${novelInfo.title}/${novelInfo.threadId}/${novelInfo.assistId}")
                    }
                },
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
                            id = if (novelInfo.assistId == ContextCompat.getString(
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
                            text = if (novelInfo.assistId == ContextCompat.getString(
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
                        .clickable {
                            onClicked(novelInfo)
                        },
                    composition = fireLottie,
                    iterations = LottieConstants.IterateForever
                )
            }
        }
    }
}
