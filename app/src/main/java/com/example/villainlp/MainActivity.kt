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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunRequest
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



@OptIn(BetaOpenAI::class)
class MainActivity : ComponentActivity() {
    private val token = "api key 적기"
    private val openAI by lazy { OpenAI(token) }
    private var assistantId = AssistantId("asst 키 적기")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            val assistantResponse = openAI.assistant(
                request = AssistantRequest(
                    name = "novel writer",
                    instructions = "You will take turns with the user to write a novel.",
                    tools = listOf(AssistantTool.CodeInterpreter),
                    model = ModelId("gpt-3.5-turbo-16k")
                )
            )
            assistantId = assistantResponse.id
        }
        setContent {
            VillainlpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d("MainActivity", "Surface initialized")
                    VillainNavigation(openAI)
                    UserPrompt(openAI, assistantId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPrompt(openAI: OpenAI, assistantId: AssistantId?) {
    val (input, setInput) = remember { mutableStateOf("") }
    val (output, setOutput) = remember { mutableStateOf("") }

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
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("UserPrompt", "CoroutineScope launched")
                    val thread = openAI.thread()
                    // user의 message를 assistant에게 전달
                    openAI.message(
                        threadId = thread.id,
                        request = MessageRequest(
                            role = Role.User,
                            content = input
                        )
                    )
                    // assistant가 작성한 novel을 가져옴
                    val run = openAI.createRun(
                        thread.id,
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
                            threadId = thread.id,
                            runId = run.id
                        )
                    } while (retrievedRun.status != Status.Completed)

                    setInput("")

                    val assistantMessages = openAI.messages(thread.id)
                    val response = assistantMessages.joinToString("\n") { message ->
                        val textContent = message.content.first() as? MessageContent.Text
                        textContent?.text?.value ?: ""
                    }
                    Log.d("UserPrompt", "response: $response")
                    setOutput(response)
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
