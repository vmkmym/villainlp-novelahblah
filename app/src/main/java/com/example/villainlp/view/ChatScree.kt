package com.example.villainlp.view

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.client.OpenAI
import com.example.villainlp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class, BetaOpenAI::class)
@Composable
fun ChatScreen() {
    var chatText by remember { mutableStateOf("") }
    var chatResponse by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val textInputService = LocalTextInputService.current
    val view = LocalView.current


    // 1. Setup client
    val token = "api-key"
    val openAI = OpenAI(token)

    var assistant by remember { mutableStateOf<Assistant?>(null) }
    var thread by remember { mutableStateOf<Thread?>(null) }

    LaunchedEffect(Unit) {
        val assistantId = "asst_key"
//        assistant = openAI.assistant(id = AssistantId(assistantId))
//        Log.d("assistant instructions", "getChatCompletion: ${assistant?.instructions}")
//        assistant = openAI.assistant(
//            id = AssistantId(assistantId), request = AssistantRequest(
//                instructions = "1. You and I are co-writers collaborating on a novel through a relay.\n" +
//                        "2. I will initiate the story from the very beginning.\n" +
//                        "3. Once I write a segment of the story, you should comprehend the user's narrative seamlessly and continue developing the story without interruptions.\n" +
//                        "4. Maintain consistency in tone, style, language, and format with the story I write, ensuring it aligns with the user's input.\n" +
//                        "5. Continue the narrative using the storytelling perspective I established initially. For example, if I use the protagonist's name, maintain an omniscient authorial perspective, and if I use \"I,\" continue with a first-person narrative.\n" +
//                        "6. Understand and preserve the established background, character traits, and categories to uphold the world-building elements. For instance, if the setting is historical, incorporate appropriate language and cultural nuances; if it's science fiction, integrate the user's imaginative elements.\n" +
//                        "7. Do not deviate from or destruct the story we co-write.\n" +
//                        "8. Adapt seamlessly to sudden introductions of new characters, environments, or storylines provided by me.\n" +
//                        "9. Handle unexpected inputs creatively and integrate them coherently into the storyline.\n" +
//                        "10. Provide approximately 20 sentences of narrative content, regardless of the length of my input.\n" +
//                        "11. Do not conclude the story until the user inputs \"-The end of the story-.\"\n" +
//                        "12. If I write \"The end\", show me the entire novel that I wrote with you from the beginning to the end without any omissions. Also, correct any grammatical errors in the user's input during this process.",
//                tools = listOf(AssistantTool.RetrievalTool),
//                model = ModelId("gpt-3.5-turbo-1106"),
//            )
//        )
        assistant = openAI.assistant(
            request = AssistantRequest(
                name = "Novel helper",
                instructions = "1. You and I are co-writers collaborating on a novel through a relay.\n" +
                        "2. I will initiate the story from the very beginning.\n" +
                        "3. Once I write a segment of the story, you should comprehend the user's narrative seamlessly and continue developing the story without interruptions.\n" +
                        "4. Maintain consistency in tone, style, language, and format with the story I write, ensuring it aligns with the user's input.\n" +
                        "5. Continue the narrative using the storytelling perspective I established initially. For example, if I use the protagonist's name, maintain an omniscient authorial perspective, and if I use \"I,\" continue with a first-person narrative.\n" +
                        "6. Understand and preserve the established background, character traits, and categories to uphold the world-building elements. For instance, if the setting is historical, incorporate appropriate language and cultural nuances; if it's science fiction, integrate the user's imaginative elements.\n" +
                        "7. Do not deviate from or destruct the story we co-write.\n" +
                        "8. Adapt seamlessly to sudden introductions of new characters, environments, or storylines provided by me.\n" +
                        "9. Handle unexpected inputs creatively and integrate them coherently into the storyline.\n" +
                        "10. Provide approximately 20 sentences of narrative content, regardless of the length of my input.\n" +
                        "11. Do not conclude the story until the user inputs \"-The end of the story-.\"\n" +
                        "12. If I write \"The end\", show me the entire novel that I wrote with you from the beginning to the end without any omissions. Also, correct any grammatical errors in the user's input during this process.",
                tools = listOf(AssistantTool.CodeInterpreter),
                model = ModelId("gpt-3.5-turbo")
            )
        )
        thread = openAI.thread()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat area
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = chatResponse,
                style = TextStyle(color = Color.Black) // 예시: 검은색으로 설정
            )
        }

        // Input area
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            var isSendButtonEnabled by remember { mutableStateOf(true) }

            // Chat input
            OutlinedTextField(
                value = chatText,
                onValueChange = {
                    chatText = it
                    isSendButtonEnabled = it.isNotBlank()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (isSendButtonEnabled) {
                            // 4. Add a message to the thread
                            scope.launch {
                                if (thread != null) {
                                    openAI.message(
                                        threadId = thread!!.id,
                                        request = MessageRequest(
                                            role = Role.User,
                                            content = chatText
                                        )
                                    )
                                    val messages = openAI.messages(thread!!.id)
                                    println("List of messages in the thread:")
                                    for (message in messages) {
                                        val textContent =
                                            message.content.first() as? MessageContent.Text
                                                ?: error("Expected MessageContent.Text")
                                        println(textContent.text.value)
                                        Log.d(
                                            "ChatGptApiClient",
                                            "getChatCompletion: ${textContent.text.value}"
                                        )
                                    }
                                    // 5. Run the assistant
                                    val run = openAI.createRun(
                                        thread!!.id,
                                        request = RunRequest(
                                            assistantId = assistant?.id ?: error("Expected Assistant"),
                                        )
                                    )
                                    // 6. Check the run status
                                    do {
                                        delay(150)
                                        val retrievedRun = openAI.getRun(threadId = thread!!.id, runId = run.id)
                                    } while (retrievedRun.status != Status.Completed)
                                    // 6. Display the assistant's response
                                    chatResponse = getChatMessage(openAI, thread!!)
                                }
                            }
                            chatText = ""
                            keyboardController?.hide()
                        }
                    }
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_message_24),
                        contentDescription = null
                    )
                }
            )

            // Send button
            Button(
                onClick = {
                    scope.launch {
                        if (thread != null) {
                            openAI.message(
                                threadId = thread!!.id,
                                request = MessageRequest(
                                    role = Role.User,
                                    content = chatText
                                )
                            )
                            val messages = openAI.messages(thread!!.id)
                            println("List of messages in the thread:")
                            for (message in messages) {
                                val textContent =
                                    message.content.first() as? MessageContent.Text
                                        ?: error("Expected MessageContent.Text")
                                println(textContent.text.value)
                                Log.d(
                                    "ChatGptApiClient",
                                    "getChatCompletion: ${textContent.text.value}"
                                )
                            }
                            // 5. Run the assistant
                            val run = openAI.createRun(
                                thread!!.id,
                                request = RunRequest(
                                    assistantId = assistant?.id ?: error("Expected Assistant"),
                                )
                            )
                            // 6. Check the run status
                            do {
                                delay(1500)
                                val retrievedRun = openAI.getRun(threadId = thread!!.id, runId = run.id)
                            } while (retrievedRun.status != Status.Completed)

                            // 6. Display the assistant's response
                            chatResponse = getChatMessage(openAI, thread!!)
                        }
                    }
                    chatText = ""
                },
                enabled = isSendButtonEnabled
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

@OptIn(BetaOpenAI::class)
suspend fun getChatMessage(openAI: OpenAI, thread: Thread): String {
    // 6. Display the assistant's response
    val assistantMessages = openAI.messages(thread.id)
    for (message in assistantMessages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        Log.d("ChatGptApiClient", "getChatCompletion: ${textContent.text.value}")
        return textContent.text.value
    }
    return "Not found"
}