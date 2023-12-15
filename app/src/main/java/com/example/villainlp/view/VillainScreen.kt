@file:OptIn(BetaOpenAI::class)

package com.example.villainlp.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import com.example.villainlp.R
import com.example.villainlp.model.ChatMessage
import com.example.villainlp.model.ChatbotMessage
//import com.example.villainlp.model.ChatbotMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

@Composable
fun LoginScreen(signInClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_login),
            contentDescription = "구글로그인",
            modifier = Modifier
                .size(250.dp, 100.dp)
                .clickable { signInClicked() }
        )
    }
}

@Composable
fun Logout(signOutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(onClick = { signOutClicked() }) {
            Text(text = "LogOut")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, BetaOpenAI::class)
@Composable
fun HomeScreen(navController: NavController, firebaseAuth: FirebaseAuth) {
    val context = LocalContext.current
    val (input, setInput) = remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val assistantKey = getString(context, R.string.assistant_key)
    val openAI by lazy { OpenAI(token) }
    var assistantId by remember { mutableStateOf<AssistantId?>(null) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }

    val user: FirebaseUser? = firebaseAuth.currentUser
    var sentMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var sentBotMessages by remember { mutableStateOf(listOf<ChatbotMessage>()) }


    val title = ""

    // 새 메시지를 받아올 때마다 UI를 업데이트하기 위해 loadChatMessages 함수 호출
    loadChatMessages({ messages -> sentMessages = messages }, title, threadId)
    loadChatBotMessages({ botmessages -> sentBotMessages = botmessages }, title, threadId)

    LaunchedEffect(Unit) {
        // assistantId 가져와서 사용하기
        val assistantResponse = openAI.assistant(AssistantId(assistantKey))
        if (assistantResponse != null) {
            Log.d(
                "assistant instructions",
                "getChatCompletion: ${assistantResponse.instructions}, ${assistantResponse.id}"
            )
            assistantId = assistantResponse.id
        }
        val thread = openAI.thread()
        threadId = thread.id

        // 새 메시지를 받아올 때마다 UI를 업데이트하기 위해 loadChatMessages 함수 호출
        loadChatMessages({ messages -> sentMessages = messages }, title, threadId)
        loadChatBotMessages({ botmessages -> sentBotMessages = botmessages }, title, threadId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Messenger",
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif
                    ) },
                navigationIcon = {
                    IconButton(onClick = { /* 창작마당으로 이동 */
                        navController.navigate("CreateGroundScreen")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기(홈화면)"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 채팅 내용 검색 동작 */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "검색"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
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
//                            val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
//                                Date()
//                            )
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
                        if (threadId != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.d("UserPrompt", "CoroutineScope launched")
                                // user의 message를 assistant에게 전달
                                openAI.message(
                                    threadId = threadId!!,
                                    request = MessageRequest(
                                        role = Role.User,
                                        content = input
                                    )
                                )
                                Log.d("threadId", "threadId: $threadId")
                                // assistant가 작성한 novel을 가져옴
                                val run = openAI.createRun(
                                    threadId!!,
                                    request = RunRequest(
                                        assistantId = assistantId ?: return@launch,
                                        instructions = "The user wants you to continue writing the novel. Please continue writing the novel.",
                                    )
                                )
                                var retrievedRun: Run
                                // assistant가 작성한 novel이 완성될 때까지 기다리면서 응답 중 이미지 띄우기
                                do {
                                    delay(150)
                                    retrievedRun = openAI.getRun(
                                        threadId = threadId!!,
                                        runId = run.id
                                    )
                                } while (retrievedRun.status != Status.Completed)

                                setInput("")

                                val assistantMessages = openAI.messages(threadId!!)
                                val response = assistantMessages.joinToString("\n") { message ->
                                    val textContent =
                                        message.content.first() as? MessageContent.Text
                                    textContent?.text?.value ?: ""
                                }

//                                // 사용자가 입력한 메시지에 대한 Assistant의 응답을 가져옵니다.
//                                val assistantMessages = openAI.messages(threadId!!)
//                                val response = assistantMessages.firstOrNull { message ->
//                                    val textContent =
//                                        message.content.firstOrNull() as? MessageContent.Text
//                                    textContent?.text?.value == ""
//                                }
//                                response?.let {
//                                    val textContent = it.content.firstOrNull() as? MessageContent.Text
//                                    val assistantResponse = textContent?.text?.value ?: ""
//                                    // 여기에 assistantResponse를 사용하여 처리 로직을 추가합니다.
//                                    // assistantResponse는 사용자 입력에 관련된 Assistant의 응답을 포함합니다.
//                                }

                                Log.d("UserPrompt", "response: $response")

                                val newChatbotMessage =
                                    ChatbotMessage(
                                        message = response,
                                        uploadDate = currentDate
                                    )
                                saveChatbotMessage(newChatbotMessage, title, threadId)
                            }
                        }
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
        ) {
            items(sentMessages.reversed()) { message ->
                ChatItemBubble(
                    message = message,
                    userId = user?.uid
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .heightIn(50.dp, 50.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f) // 여기서 비율을 조정
                .padding(horizontal = 8.dp, vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            singleLine = false
        )
        IconButton(
            onClick = { onSendClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "메시지 전송 버튼"
            )
        }
    }
}

@Composable
fun ChatItemBubble(
    message: ChatMessage,
    userId: String?
) {
    val isCurrentUserMessage = userId == message.userId
    val bubbleColor = if (isCurrentUserMessage) Color(0xFF17C3CE) else Color(0xFF646E6F)
    val horizontalArrangement = if (isCurrentUserMessage) Arrangement.End else Arrangement.Start

    if (!isCurrentUserMessage) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = horizontalArrangement,
//                modifier = Modifier
//                    .fillMaxWidth(),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.userimage),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = message.userName ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = bubbleColor,
                                shape = RoundedCornerShape(
                                    topEnd = 25.dp,
                                    topStart = 25.dp,
                                    bottomEnd = 25.dp,
                                    bottomStart = 0.dp
                                )
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
                Text(
                    text = message.uploadDate ?: "",
                    fontSize = 9.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }

    if (isCurrentUserMessage) {
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = message.uploadDate ?: "",
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Box(
                modifier = Modifier
                    .background(
                        color = bubbleColor,
                        shape = RoundedCornerShape(
                            topEnd = 25.dp,
                            topStart = 25.dp,
                            bottomEnd = 25.dp,
                            bottomStart = 0.dp
                        )
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


// Firebase에서 특정 제목 아래에 채팅 메시지 저장하는 함수
fun saveChatMessage(chatMessage: ChatMessage, title: String, threadId: ThreadId?) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title") // title은 채팅방 이름
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatMessage)

    // 해당 대화에 대한 threadId도 저장
    newMessageRef.child("threadId").setValue(threadId.toString())
}

fun saveChatbotMessage(chatbotMessage: ChatbotMessage, title: String, threadId: ThreadId?) {
    val database = Firebase.database
    val chatRef = database.getReference("cute/$title") // title은 채팅방 이름
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatbotMessage)

    // 해당 대화에 대한 threadId도 저장
    newMessageRef.child("threadId").setValue(threadId.toString())
}


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

fun loadChatBotMessages(listener: (List<ChatbotMessage>) -> Unit, title: String, threadId: ThreadId?) {
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