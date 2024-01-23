@file:OptIn(BetaOpenAI::class)

package com.example.villainlp.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.server.FirebaseTools.saveChatToNovel
import com.example.villainlp.library.RelayChatToNovelBook
import com.example.villainlp.shared.Screen
import com.example.villainlp.ui.theme.Blue789
import com.example.villainlp.library.addFocusCleaner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, BetaOpenAI::class)
@Composable
fun ChattingScreen(
    navController: NavController,
    auth: FirebaseAuth,
    title: String,
    threadID: String,
    assistantKey: String,
) {
    val context = LocalContext.current
    val (input, setInput) = remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val openAI by lazy { OpenAI(token) }
    var assistantId by remember { mutableStateOf<AssistantId?>(null) }
    val threadId = ThreadId(threadID)
    var instructions by remember { mutableStateOf("") }

    var sentMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var sentBotMessages by remember { mutableStateOf(listOf<ChatbotMessage>()) }

    var showDialog by remember { mutableStateOf(false) }
    val user = auth.currentUser

    var isAnimationRunning by remember { mutableStateOf(false) }
    val firePuppleLottie by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple)
    )
    val loadingAnimation by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        // assistantId 가져와서 사용하기
        val assistantResponse = openAI.assistant(AssistantId(assistantKey))
        if (assistantResponse != null) {
            Log.d(
                "assistant instructions",
                "getChatCompletion: ${assistantResponse.instructions}, ${assistantResponse.id}"
            )
            assistantId = assistantResponse.id
            instructions = assistantResponse.instructions ?: ""
        }

        // 새 메시지를 받아올 때마다 UI를 업데이트하기 위해 loadChatMessages 함수 호출
        loadChatMessages({ messages -> sentMessages = messages }, title, threadId)
        loadChatBotMessages({ botmessages -> sentBotMessages = botmessages }, title, threadId)
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
                            imageVector = Icons.Default.ArrowBack,
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
                    onValueChange = { setInput(it) }
                ) {
                    val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                        Date()
                    )
                    if (input.isNotEmpty()) {
                        val newChatMessage =
                            ChatMessage(
                                message = input,
                                userId = user?.uid,
                                userName = user?.displayName,
                                uploadDate = currentDate
                            )
                        saveChatMessage(newChatMessage, title, threadId)
                    }
                    // 챗봇 대답
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("UserPrompt", "CoroutineScope launched")
                        // user의 message를 assistant에게 전달
                        openAI.message(
                            threadId = threadId,
                            request = MessageRequest(
                                role = Role.User,
                                content = input
                            )
                        )
                        Log.d("threadId", "threadId: $threadId")
                        // assistant가 작성한 novel을 가져옴
                        val run = openAI.createRun(
                            threadId,
                            request = RunRequest(
                                assistantId = assistantId ?: return@launch,
                                instructions = instructions,
                            )
                        )

                        setInput("")
                        isAnimationRunning = true

                        var retrievedRun: Run
                        do {
                            delay(150)
                            retrievedRun = openAI.getRun(
                                threadId = threadId,
                                runId = run.id
                            )
                        } while (retrievedRun.status != Status.Completed)


                        // 챗봇이 내준 마지막 문장을 가져옴
                        val assistantMessages = openAI.messages(threadId)
                        val lastAssistantMessage = assistantMessages
                            .filter { it.role == Role.Assistant }  // Assistant의 메시지만 필터링
                            .firstOrNull()  // 마지막 메시지만 가져옴 .lastOrNull 이렇게 하면 첫문장만 가져옴

                        val response = if (lastAssistantMessage != null) {
                            val textContent =
                                lastAssistantMessage.content.first() as? MessageContent.Text
                            textContent?.text?.value ?: ""
                        } else {
                            ""
                        }

                        Log.d("UserPrompt", "response: $response")

                        val newChatbotMessage =
                            ChatbotMessage(
                                message = response,
                                uploadDate = currentDate
                            )
                        saveChatbotMessage(newChatbotMessage, title, threadId)
                        isAnimationRunning = false

                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
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
                    userId = user?.uid
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = firePuppleLottie,
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
                            // thread의 채팅내역을 시간순으로 보여줌, 나 -> 봇 -> 나 -> 봇 이 순서로 작동
                            val assistantMessages = openAI.messages(threadId)
                            val reversedMessages = assistantMessages.reversed()
                            val response = reversedMessages.joinToString("\n\n") { message ->
                                val textContent =
                                    message.content.first() as? MessageContent.Text
                                textContent?.text?.value ?: ""
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
                            saveChatToNovel(myRelayNovel)
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
                            color = Blue789
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
                            color = Blue789
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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
        Divider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
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
                    if (!isTextFieldFocused) {
                        Text(
                            text = "당신의 이야기를 써주세요 :)",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFBBBBBB)
                            )
                        )
                    } else {
                        Text(
                            text =
                            "작가의 마당 : 지금 작업중인 소설의 설정을 써주세요.\n" +
                                    "꿈의 마당 : 지금 생각나는 이야기를 써주세요.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFBBBBBB)
                            )
                        )
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = false
            )
            // send 버튼 이미지 수정했음IconButton(
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
    }
}

@Composable
fun ChatItemBubble(
    message: ChatMessage,
    userId: String?,
) {
    val isCurrentUserMessage = userId == message.userId
    val bubbleColor = if (isCurrentUserMessage) Color(0xFF3CDEE9) else Color(0xFFFFFFFF)
    val horizontalArrangement = if (isCurrentUserMessage) Arrangement.End else Arrangement.Start
    val bubbleShape =
        if (isCurrentUserMessage) {
            RoundedCornerShape(
                topEnd = 28.dp,
                topStart = 28.dp,
                bottomEnd = 28.dp,
                bottomStart = 28.dp
            )
        } else {
            RoundedCornerShape(
                topEnd = 28.dp,
                topStart = 28.dp,
                bottomEnd = 28.dp,
                bottomStart = 28.dp
            )
        }
// 챗봇 메시지
    ChatbotResponse(isCurrentUserMessage, message, bubbleColor, bubbleShape)
    // 유저가 보낸 메시지
    UserResponse(isCurrentUserMessage, message, bubbleColor, bubbleShape)

}
@Composable
private fun UserResponse(
    isCurrentUserMessage: Boolean,
    message: ChatMessage,
    bubbleColor: Color,
    bubbleShape: RoundedCornerShape,
) {
    if (isCurrentUserMessage) {
        Column(
            modifier = Modifier.padding(start = 50.dp, end = 15.dp, top = 20.dp, bottom = 20.dp)
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
                        .background(
                            color = bubbleColor,
                            shape = bubbleShape
                        )
                        .padding(6.dp)
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
}

@Composable
private fun ChatbotResponse(
    isCurrentUserMessage: Boolean,
    message: ChatMessage,
    bubbleColor: Color,
    bubbleShape: RoundedCornerShape,
) {
    if (!isCurrentUserMessage) {
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
                    text = message.userName ?: "",
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            // 말풍선
            Column(
                modifier = Modifier.padding(start = 25.dp, end = 50.dp, bottom = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = bubbleColor,
                                shape = bubbleShape
                            )
                            .border(
                                width = 2.dp,
                                color = Color(0xFF3CDEE9),
                                shape = bubbleShape
                            )
                            .padding(6.dp)
                    ) {
                        Text(
                            text = message.message ?: "",
                            color = Color(0xFF646E6F),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Text(
                        text = message.uploadDate ?: "",
                        fontSize = 9.sp,
                        modifier = Modifier.padding(end = 3.dp, bottom = 3.dp),
                    )
                }
            }
        }
    }
}



// Firebase에서 특정 제목 아래에 채팅 메시지 저장하는 함수
@OptIn(BetaOpenAI::class)
fun saveChatMessage(chatMessage: ChatMessage, title: String, threadId: ThreadId?) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title") // title은 채팅방 이름
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatMessage)

    // 해당 대화에 대한 threadId도 저장
    newMessageRef.child("threadId").setValue(threadId.toString())
}

@OptIn(BetaOpenAI::class)
fun saveChatbotMessage(chatbotMessage: ChatbotMessage, title: String, threadId: ThreadId?) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title") // title은 채팅방 이름
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatbotMessage)

    // 해당 대화에 대한 threadId도 저장
    newMessageRef.child("threadId").setValue(threadId.toString())
}


@OptIn(BetaOpenAI::class)
fun loadChatMessages(listener: (List<ChatMessage>) -> Unit, title: String, threadId: ThreadId?) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title")

    // 대화 스레드에 대한 쿼리를 추가하여 해당 스레드의 메시지만 가져옵니다.
    val query = chatRef.orderByChild("threadId").equalTo(threadId.toString())

    // 쿼리를 이용해서 데이터베이스에서 데이터를 가져오는 로직
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<ChatMessage>()
            for (childSnapshot in snapshot.children) {
                val chatMessage = childSnapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    messages.add(it)
                }
            }
            listener(messages)
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("Load Chat Message", "채팅 내용 로드하기 실패!!", error.toException())
        }
    })
}

@OptIn(BetaOpenAI::class)
fun loadChatBotMessages(
    listener: (List<ChatbotMessage>) -> Unit,
    title: String,
    threadId: ThreadId?,
) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title")

    // 대화 스레드에 대한 쿼리를 추가하여 해당 스레드의 메시지만 가져옵니다.
    val query = chatRef.orderByChild("threadId").equalTo(threadId.toString())

    // 쿼리를 이용해서 데이터베이스에서 데이터를 가져오는 로직
    query.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messages = mutableListOf<ChatbotMessage>()
            for (childSnapshot in snapshot.children) {
                val chatMessage = childSnapshot.getValue(ChatbotMessage::class.java)
                chatMessage?.let {
                    messages.add(it)
                }
            }
            listener(messages)
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("Load Chat Message", "채팅 내용 로드하기 실패!!", error.toException())
        }
    })
}
