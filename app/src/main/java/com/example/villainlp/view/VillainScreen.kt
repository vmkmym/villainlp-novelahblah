package com.example.villainlp.view

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.example.villainlp.R
import com.example.villainlp.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
fun SettingScreen(signOutClicked: () -> Unit) {
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
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val (input, setInput) = remember { mutableStateOf("") }
    val (output, setOutput) = remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val assistantKey = getString(context, R.string.assistant_key)
    val openAI by lazy { OpenAI(token) }
    var assistantId by remember { mutableStateOf<AssistantId?>(null) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }

    var assistantInstruction by remember { mutableStateOf("")}

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

        // assistantId 가져와서 설정 바꾸고 사용하기
//        val assistantResponse = openAI.assistant(
//            id = AssistantId(assistant_key), request = AssistantRequest(
//                instructions = "/* TODO : 누링이 주는 instructions를 넣고 실행해 보세요 */",
//                tools = listOf(AssistantTool.RetrievalTool),
//                model = ModelId("gpt-3.5-turbo-1106"),
//            )
//        )
//        assistantId = assistantResponse.id
//        assistantInstruction = assistantResponse.instructions?: ""

        val thread = openAI.thread()
        threadId = thread.id
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        TextField(
            value = input,
            onValueChange = setInput,
            label = { Text("소설 내용을 입력하세요") }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            // Display user input message
            Message(
                text = "You",
                isBot = false
            )
            Spacer(modifier = Modifier.width(5.dp))
            Message(
                text = input,
                isBot = false
            )
        }

        Button(
            onClick = {
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
                                instructions = assistantInstruction,
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
                            val textContent = message.content.first() as? MessageContent.Text
                            textContent?.text?.value ?: ""
                        }
                        Log.d("UserPrompt", "response: $response")
                        setOutput(response)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Send")
        }

//        Button(onClick = { navController.navigate(Screen.Logout.route) }) {
//            Text(text = "logout")
//        }

        if (output.isNotEmpty()) {
            Text(text = output)
        }
    }
}
