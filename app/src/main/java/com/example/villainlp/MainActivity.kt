@file:OptIn(BetaOpenAI::class, BetaOpenAI::class)

package com.example.villainlp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import com.example.villainlp.model.Message
import com.example.villainlp.model.Screen
import com.example.villainlp.ui.theme.VillainlpTheme
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.HomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VillainlpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d("MainActivity", "Surface initialized")
//                    VillainNavigation(openAI)
                    UserPrompt()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPrompt() {
    val (input, setInput) = remember { mutableStateOf("") }
    val (output, setOutput) = remember { mutableStateOf("") }
    val token = "Your API Key"
    val assistantKey = "Your Assistant Key"
    val openAI by lazy { OpenAI(token) }
    var assistantId by remember { mutableStateOf<AssistantId?>(null) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }

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

        if (output.isNotEmpty()) {
            Text(text = output)
        }
    }
}


@Composable
fun VillainNavigation(openAI: OpenAI) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Screen1.route) {
        composable(Screen.Screen1.route) {
            // Pass OpenAI instance to the LoginScreen
            LoginScreen(navController, openAI)
        }
        composable(Screen.Screen2.route) {
            // Pass OpenAI instance to the HomeScreen
            HomeScreen(navController, openAI)
        }
    }
}
