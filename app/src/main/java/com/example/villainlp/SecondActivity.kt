@file:OptIn(BetaOpenAI::class)
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

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VillainlpTheme {
                // Initialize OpenAI instance
                val token = "여기에 api key를 넣어주세요"
                Log.d("token", token)
                val openAI = remember { OpenAI(token) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Log.d("MainActivity", "Surface initialized")
                    VillainNavigation(openAI)
                    UserPrompt(openAI)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPrompt(openAI: OpenAI) {
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
            label = { Text("Enter your message") }
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
                Log.d("UserPrompt", "CoroutineScope launched")
                // OpenAI로 사용자 입력 및 통신하기 위한 코루틴 실행
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("UserPrompt", "CoroutineScope launched")
                    // thread 생성
                    val thread = openAI.thread()

                    // user의 message를 assistant에게 전달
                    openAI.message(
                        threadId = thread.id,
                        request = MessageRequest(
                            role = Role.User,
                            content = input
                        )
                    )

                    // 어시스턴트 생성
                    val assistant = openAI.assistant(
                        request = AssistantRequest(
                            name = "novel writer",
                            instructions = "You will take turns with the user to write a novel.",
                            tools = listOf(AssistantTool.CodeInterpreter),
                            model = ModelId("gpt-3.5-turbo-16k")
                        )
                    )

                    // 어시스턴트 ID 저장
                    val assistantId = assistant.id

                    val run = openAI.createRun(
                        thread.id,
                        request = RunRequest(
                            assistantId = assistantId,
                            instructions = "The user wants you to continue writing the novel. Please continue writing the novel.",
                        )
                    )

                    // assistant 작업이 완료될 때까지 주기적으로 작업 상태 확인
                    var isCompleted = false
                    while (!isCompleted) {
                        val retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
                        isCompleted = retrievedRun.status == Status.Completed
                        delay(100) // 일정 간격으로 작업 상태 확인
                    }

                    // 메시지를 보내면 input을 비움
                    setInput("")

                    // assistant의 응답을 처리하여 output 업데이트
                    val assistantMessages = openAI.messages(thread.id)
                    val response = assistantMessages.joinToString("\n") { message ->
                        val textContent = message.content.first() as? MessageContent.Text
                        textContent?.text?.value ?: ""
                    }
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
