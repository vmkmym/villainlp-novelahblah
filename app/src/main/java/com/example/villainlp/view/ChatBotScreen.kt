package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChatBotScreen(navController: NavController) {
    var text by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "ChatBot") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Bottom,
            ) {
                items(messages) { message ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(text = message)
                    }
                }
            }
        },
        bottomBar = {
            //중앙에 채팅을 칠수 있게 TextFiled를 만들어야함 Text가 길어지면 크기는 동일하되 다음줄로 넘어가겠끔 만들것, 그리고 버튼을 누르면 채팅이 나오게 만들것
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                )
                Button(
                    onClick = {
                        messages = messages + text
                        text = ""
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Send")
                }
            }
        }
    )
}

class ChatGPTClient {

    companion object {
        private const val API_ENDPOINT = "YOUR_CHATGPT_API_ENDPOINT"
        private const val API_KEY = "YOUR_CHATGPT_API_KEY"
    }

    interface ChatGPTResponseListener {
        fun onResponse(response: String)
        fun onError(e: Exception)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun sendRequest(input: String, listener: ChatGPTResponseListener) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val mediaType = "application/json".toMediaTypeOrNull()
                val body = RequestBody.create(
                    mediaType,
                    "{\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"$input\"}]}"
                )

                val request = Request.Builder()
                    .url(API_ENDPOINT)
                    .post(body)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    launch(Dispatchers.Main) {
                        listener.onResponse(responseBody)
                    }
                } else {
                    throw Exception("Request failed with code: ${response.code}")
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    listener.onError(e)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ChatScreen() {
    var userInput by remember { mutableStateOf("") }
    var chatResponse by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val chatGPTClient = remember { ChatGPTClient() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // User Input
        OutlinedTextField(
            value = userInput,
            onValueChange = {
                userInput = it
            },
            label = { Text("Type your message") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    // Send button clicked
//                    sendMessage(chatGPTClient, userInput, chatResponse, isLoading)
                    if (!isLoading) {
                        GlobalScope.launch(Dispatchers.Main) {
                            chatGPTClient.sendRequest(userInput, object : ChatGPTClient.ChatGPTResponseListener {
                                override fun onResponse(response: String) {
                                    // Update UI with the response
                                    // Use remember to trigger recomposition
                                    chatResponse = response
                                }

                                override fun onError(e: Exception) {
                                    // Handle error
                                }
                            })
                        }
                    }

                }
            )
        )

        // Send Button
        Button(
            onClick = {
//                sendMessage(chatGPTClient, userInput, chatResponse, isLoading)
                if (!isLoading) {
                    GlobalScope.launch(Dispatchers.Main) {
                        chatGPTClient.sendRequest(userInput, object : ChatGPTClient.ChatGPTResponseListener {
                            override fun onResponse(response: String) {
                                // Update UI with the response
                                // Use remember to trigger recomposition
                                chatResponse = response
                            }

                            override fun onError(e: Exception) {
                                // Handle error
                            }
                        })
                    }
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Send")
        }

        // Chat Response
        Text(
            text = chatResponse,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//@OptIn(DelicateCoroutinesApi::class)
//fun sendMessage(
//    chatGPTClient: ChatGPTClient,
//    userInput: String,
//    chatResponse: String,
//    isLoading: Boolean
//) {
//    if (!isLoading) {
//        GlobalScope.launch(Dispatchers.Main) {
//            chatGPTClient.sendRequest(userInput, object : ChatGPTClient.ChatGPTResponseListener {
//                override fun onResponse(response: String) {
//                    // Update UI with the response
//                    // Use remember to trigger recomposition
//                    chatResponse = response
//                }
//
//                override fun onError(e: Exception) {
//                    // Handle error
//                }
//            })
//        }
//    }
//}