package com.example.villainlp.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.villainlp.R
import com.example.villainlp.model.ChatGptApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Properties

//@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen() {
//    var chatText by remember { mutableStateOf("") }
//    val chatResponse by remember { mutableStateOf("") }
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val textInputService = LocalTextInputService.current
//    val view = LocalView.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Chat area
//        Box(
//            modifier = Modifier
//                .weight(1f)
//                .padding(8.dp)
//        ) {
//            Text(
//                text = chatResponse,
//                style = TextStyle(color = Color.Black) // 예시: 검은색으로 설정
//            )
//        }
//
//        // Input area
//        Row(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth()
//        ) {
//            var isSendButtonEnabled by remember { mutableStateOf(true) }
//
//            // Chat input
//            OutlinedTextField(
//                value = chatText,
//                onValueChange = {
//                    chatText = it
//                    isSendButtonEnabled = it.isNotBlank()
//                },
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(end = 8.dp),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = ImeAction.Send
//                ),
//                keyboardActions = KeyboardActions(
//                    onSend = {
//                        if (isSendButtonEnabled) {
//                            sendMessage(chatText, context, scope)
//                            chatText = ""
//                            keyboardController?.hide()
//                        }
//                    }
//                ),
//                leadingIcon = {
//                    Icon(
//                        painter = painterResource(id = R.drawable.baseline_message_24),
//                        contentDescription = null
//                    )
//                }
//            )
//
//            // Send button
//            Button(
//                onClick = {
//                    sendMessage(chatText, context, scope)
//                    chatText = ""
//                },
//                enabled = isSendButtonEnabled
//            ) {
//                Icon(imageVector = Icons.Default.Send, contentDescription = null)
//            }
//        }
//    }
//}
//
//private fun sendMessage(message: String, context: Context, scope: CoroutineScope) {
//    val apiKey = getApiKey(context) // Function to retrieve API key from local properties
//    val chatGptApiClient = ChatGptApiClient(apiKey)
//
//    scope.launch {
//        val response = chatGptApiClient.getChatCompletion(message)
//        // Update UI with the response
//        // For example: chatResponse = response
//    }
//}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    var chatText by remember { mutableStateOf("") }
    val chatList = remember { mutableStateListOf<String>() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat area using LazyColumn
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(chatList) { chat ->
                Text(
                    text = chat,
                    style = TextStyle(color = Color.Black) // 예시: 검은색으로 설정
                )
            }
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
                            scope.launch {
                                sendMessage(chatText, context, scope) { response ->
                                    // Update chatList with the response
                                    chatList.add(response)
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
                        sendMessage(chatText, context, scope) { response ->
                            // Update chatList with the response
                            chatList.add(response)
                        }
                    }
//                    sendMessage(chatText, context, scope) { response ->
//                        // Update chatList with the response
//                        chatList.add(response)
//                    }
                    chatText = ""
                },
                enabled = isSendButtonEnabled
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        }
    }
}

private suspend fun sendMessage(
    message: String,
    context: Context,
    scope: CoroutineScope,
    onResponse: (String) -> Unit
) {
    val apiKey = getApiKey(context) // Function to retrieve API key from local properties
    Log.d("ChatScreen", "sendMessage: $apiKey")
    val chatGptApiClient = ChatGptApiClient(apiKey)

    scope.launch {
        val response = chatGptApiClient.getChatCompletion(message)
        onResponse(response)
    }
}


private fun getApiKey(context: Context): String {
    val properties = Properties()
    val assetManager = context.assets
    try {
        val inputStream = assetManager.open("local.properties")
        properties.load(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return properties.getProperty("api_key", "")
}