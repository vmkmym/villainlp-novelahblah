@file:OptIn(ExperimentalFoundationApi::class)

package com.example.villainlp.chat.geminiChat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.novel.library.comment.addFocusCleaner
import com.example.villainlp.server.FirebaseTools
import com.example.villainlp.shared.RelayChatToNovelBook
import com.example.villainlp.shared.Screen
import com.example.villainlp.ui.theme.Primary
import com.example.villainlp.ui.theme.TextGray
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GeminiChatScreen(
    navController: NavController,
    auth: FirebaseAuth,
    title: String,
    uuid: String
) {
    val geminiChatViewModel: GeminiChatViewModel = viewModel(factory = GeminiViewModelFactory)
    val (input, setInput) = remember { mutableStateOf("") }
    var sentMessages by remember { mutableStateOf(listOf<GeminiChatMessage>()) }
    var sentBotMessages by remember { mutableStateOf(listOf<GeminiChatMessage>()) }
    var showDialog by remember { mutableStateOf(false) }
    val user = auth.currentUser
    var isAnimationRunning by remember { mutableStateOf(false) }
    val lottie by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple)
    )
    val loadingAnimation by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        geminiChatViewModel.loadChatMessages({ messages -> sentMessages = messages }, title, uuid)
        geminiChatViewModel.loadChatbotMessages({ botmessages -> sentBotMessages = botmessages }, title, uuid)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(isAnimationRunning) {
        if (isAnimationRunning) {
            listState.animateScrollToItem(index = 0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* 릴레이소설로 이동 */
                        navController.navigate(Screen.ChattingList.route)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로 가기(홈화면)"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.book_5),
                            contentDescription = "save as book",
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = input,
                    onValueChange = { setInput(it) },
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        setInput("")
                        isAnimationRunning = true
                        geminiChatViewModel.sendMessage(input, title, user?.uid ?: "ERROR", uuid) {
                            isAnimationRunning = false
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            reverseLayout = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .addFocusCleaner(focusManager)
        ) {
            // 챗봇 메시지의 응답이 올 때 까지 로딩 애니메이션 작동...
            item {
                if (isAnimationRunning) {
                    GenerateResponse(loadingAnimation)
                }
            }
            items(sentMessages.reversed()) { message ->
                ChatItemBubble(
                    message = message,
                    userId = user?.uid ?: "ERROR"
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = lottie,
                    iterations = LottieConstants.IterateForever
                )
            },
            onDismissRequest = { showDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            },
            text = {
                Text(
                    text = "작성한 소설을 저장하시겠습니까?",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            // 사용자와 챗봇 메시지 결합
                            val allMessages =
                                (sentMessages + sentBotMessages).distinctBy { it.message + it.uploadDate }

                            // 업로드 날짜별로 메시지 정렬
                            val sortedMessages = allMessages.sortedBy { it.uploadDate }

                            // 메시지를 문자열로 변환
                            val response = sortedMessages.joinToString("\n\n") { message ->
                                "${message.message}"
                            }

                            val currentDate =
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                                    Date()
                                )

                            val myRelayNovel = RelayChatToNovelBook(
                                title = title,
                                author = user?.displayName ?: "ERROR",
                                script = response,
                                userID = user?.uid ?: "ERROR",
                                createdDate = currentDate
                            )
                            FirebaseTools.saveAtFirebase(myRelayNovel)
                        }
                        showDialog = false
                        navController.navigate(Screen.MyBook.route)
                    }
                ) {
                    Text(
                        text = "확인",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            },
            dismissButton = {
                IconButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = "취소",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    )
                }
            },
            modifier = Modifier
                .padding(16.dp)
        )
    }
}


@Composable
private fun GenerateResponse(loadingAnimation: LottieComposition?) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxSize()
    ) {
        LottieAnimation(
            composition = loadingAnimation,
            modifier = Modifier.fillMaxWidth(),
            iterations = LottieConstants.IterateForever
        )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f) // 여기서 비율을 조정
                    .width(350.dp)
                    .height(IntrinsicSize.Min) // 높이를 내부 내용에 맞게 자동 조정
                    .focusRequester(focusRequester = focusRequester)
                    .onFocusChanged {
                        isTextFieldFocused = it.isFocused
                    },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                placeholder = {
                    val placeholderText = if (!isTextFieldFocused) {
                        "당신의 이야기를 써주세요 :)"
                    } else {
                        "작가의 마당 : 지금 작업중인 소설의 설정을 써주세요.\n꿈의 마당 : 지금 생각나는 이야기를 써주세요."
                    }
                    Text(
                        text = placeholderText,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Light,
                            color = Color(0xFFBBBBBB)
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = false
            )
            SendButton(onSendClick = onSendClick, keyboardController = keyboardController)
        }
    }
}

@Composable
private fun SendButton(onSendClick: () -> Unit, keyboardController: SoftwareKeyboardController?) {
    IconButton(
        onClick = {
            onSendClick()
            keyboardController?.hide()
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.send),
            contentDescription = "메시지 전송 버튼"
        )
    }
}

@Composable
fun ChatItemBubble(message: GeminiChatMessage, userId: String) {
    val isCurrentUserMessage = (message.userId ?: "UID ERROR") == userId
    val bubbleColor = if (isCurrentUserMessage) Color(0xFF3CDEE9) else Color(0xFFFFFFFF)
    val bubbleShape =
        RoundedCornerShape(
            topEnd = 28.dp,
            topStart = 28.dp,
            bottomEnd = 28.dp,
            bottomStart = 28.dp
        )
    if (isCurrentUserMessage) {
        UserResponse(message, bubbleColor, bubbleShape)
    } else {
        ChatbotResponse(message, bubbleShape)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun UserResponse(
    message: GeminiChatMessage,
    bubbleColor: Color,
    bubbleShape: RoundedCornerShape,
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .padding(start = 50.dp, end = 25.dp, bottom = 15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message.uploadDate ?: "",
                fontSize = 9.sp,
                modifier = Modifier.padding(end = 3.dp)
            )
            Box(
                modifier = Modifier
                    .background(color = bubbleColor, shape = bubbleShape)
                    .width(IntrinsicSize.Max)
                    .padding(6.dp)
                    .combinedClickable(
                        onClick = { /* 클릭 이벤트를 처리하는 코드를 여기에 작성하세요. */ },
                        onLongClick = { // 말풍선을 꾹 누르면 발생하는 이벤트입니다.
                            clipboardManager.setText(
                                AnnotatedString(
                                    message.message ?: ""
                                )
                            ) // 클립보드에 텍스트를 복사합니다.
                        }
                    )
            ) {
                Text(
                    text = message.message ?: "",
                    color = Color(0xFFFFFFFF),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatbotResponse(
    message: GeminiChatMessage,
    bubbleShape: RoundedCornerShape,
) {
    val clipboardManager = LocalClipboardManager.current

    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(3.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.userimage),
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .padding(start = 3.dp)
                    .size(35.dp)
                    .clip(CircleShape)
            )
            Text(
                text = message.userName?.name ?: GeminiChatParticipant.MODEL.name,
                modifier = Modifier.padding(start = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        // 말풍선
        Column(
            modifier = Modifier.padding(start = 25.dp, end = 30.dp, bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start,
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 270.dp) // 최대 너비를 200.dp로 제한
                        .background(
                            color = Color.Transparent,
                            shape = bubbleShape
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFF3CDEE9),
                            shape = bubbleShape
                        )
                        .padding(6.dp)
                        .combinedClickable(
                            onClick = { /* 클릭 이벤트를 처리하는 코드를 여기에 작성하세요. */ },
                            onLongClick = { // 클립보드에 텍스트를 복사합니다.
                                clipboardManager.setText(AnnotatedString(message.message ?: ""))
                            }
                        )
                ) {
                    Text(
                        text = message.message ?: "",
                        color = if (isSystemInDarkTheme()) Color.White else Color.DarkGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Text(
                    text = message.uploadDate ?: "",
                    color = if (isSystemInDarkTheme()) Color.White else TextGray,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(end = 3.dp, bottom = 3.dp)
                )
            }
        }
    }
}