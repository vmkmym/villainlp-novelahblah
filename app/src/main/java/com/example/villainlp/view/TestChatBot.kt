package com.example.villainlp.view

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.delay

@OptIn(BetaOpenAI::class)
suspend fun main() {

    // 1. Setup client
    val token = "api-key"
    val openAI = OpenAI(token)

    // 2. Get an existing Assistant
    val assistantId = "asst-key"
    val assistant = openAI.assistant(id = AssistantId(assistantId))


    // 3. Create a thread
    val thread = openAI.thread()

    // 4. Add a message to the thread
    openAI.message(
        threadId = thread.id,
        request = MessageRequest(
            role = Role.User,
            content = "I need to solve the equation `3x + 11 = 14`. Can you help me?"
        )
    )
    val messages = openAI.messages(thread.id)
    println("List of messages in the thread:")
    for (message in messages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
        Log.d("ChatGptApiClient", "getChatCompletion: ${textContent.text.value}")
    }

    // 5. Run the assistant
    val run = openAI.createRun(
        thread.id,
        request = RunRequest(
            assistantId = assistant?.id ?: error("Expected Assistant"),
//            instructions = "Please address the user as Jane Doe. The user has a premium account.",
        )
    )

    // 6. Check the run status
    do {
        delay(150)
        val retrievedRun = openAI.getRun(threadId = thread.id, runId = run.id)
    } while (retrievedRun.status != Status.Completed)

    // 6. Display the assistant's response
    val assistantMessages = openAI.messages(thread.id)
    println("\nThe assistant's response:")
    for (message in assistantMessages) {
        val textContent = message.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")
        println(textContent.text.value)
        Log.d("ChatGptApiClient", "getChatCompletion: ${textContent.text.value}")
    }
}
