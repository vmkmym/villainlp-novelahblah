package com.example.villainlp.view

import android.content.Intent
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
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
import com.example.villainlp.MainActivity
import com.example.villainlp.R
import com.example.villainlp.model.ChatMessage
import com.example.villainlp.model.ChatbotMessage
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



@OptIn(ExperimentalMaterial3Api::class, BetaOpenAI::class)
@Composable
fun HomeScreen(navController: NavController, firebaseAuth: FirebaseAuth) {
    val context = LocalContext.current
    var (input, setInput) = remember { mutableStateOf("") }
    var (output, setOutput) = remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val assistantKey = getString(context, R.string.assistant_key)
    val openAI by lazy { OpenAI(token) }
    var assistantId by remember { mutableStateOf<AssistantId?>(null) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }

    val user: FirebaseUser? = firebaseAuth.currentUser
    var sentMessages by remember { mutableStateOf(listOf<ChatMessage>()) }

    // 이전 채팅 메시지 불러오기
    loadChatMessages { messages -> sentMessages = messages }

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
    }
    Log.d("assID", "assistantId: $assistantId")
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
                    IconButton(onClick = { /* 메인 액티비티로 이동 */
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
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
                    IconButton(onClick = { /* 더보기 버튼을 누르면 뭐가 나와야 할까 */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "더보기")
                    }
                    CustomTextField(
                        value = input,
                        onValueChange = { setInput(it) }
                    ) {
                        if (input.isNotEmpty()) {
                            val currentDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                Date()
                            )
                            val newChatMessage =
                                ChatMessage(
                                    message = input,
                                    userId = user?.uid,
                                    userName = user?.displayName,
                                    uploadDate = currentDate
                                )
                            saveChatMessage(newChatMessage)
                            input = ""
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
                                // assistant가 작성한 novel이 완성될 때까지 기다림
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
                                Log.d("UserPrompt", "response: $response")

                                val newChatbotMessage =
                                    ChatbotMessage(
                                        message = response,
                                    )
                                saveChatbotMessage(newChatbotMessage)
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
            .heightIn(50.dp, 50.dp),
//            .fillMaxWidth(),
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
    val bubbleColor = if (isCurrentUserMessage) Color(0xFF17C3CE) else Color(0xFF17C3CE)
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

fun loadChatMessages(listener: (List<ChatMessage>) -> Unit) {
    // Write a message to the database
    val database = Firebase.database
    val chatRef = database.getReference("chat")
    // myRef.setValue("Hello, World!") // 이렇게 하면 Hello, World!가 데이터베이스에 저장됨

    // Read from the database
    chatRef.addValueEventListener(object : ValueEventListener {
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
            Log.w("Load Chat Message", "Failed to read value.", error.toException())

        }
    })
}

fun saveChatMessage(chatMessage: ChatMessage) {
    val database = Firebase.database
    val chatRef = database.getReference("chat")
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatMessage)
}

fun saveChatbotMessage(chatbotMessage: ChatbotMessage) {
    val database = Firebase.database
    val chatRef = database.getReference("chat")
    val newMessageRef = chatRef.push()
    newMessageRef.setValue(chatbotMessage)
}


