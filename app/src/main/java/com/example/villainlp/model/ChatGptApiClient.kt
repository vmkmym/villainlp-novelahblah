package com.example.villainlp.model

// ChatGptApi.kt
import android.util.Log
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
// ChatGptApiClient.kt
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// ChatRequest.kt
data class ChatRequest(val prompt: String)

// ChatResponse.kt
data class ChatResponse(val id: String, val objectType: String, val created: Long, val model: String, val usage: ChatUsage, val choices: List<ChatChoice>)

data class ChatUsage(val promptTokens: Int, val completionTokens: Int, val totalTokens: Int)

data class ChatChoice(val message: ChatMessage, val finishReason: String, val index: Int)

data class ChatMessage(val role: String, val content: String)

interface ChatGptApi {
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): Response<ChatResponse>
}

//class ChatGptApiClient(apiKey: String) {
//
//    private val chatGptApi: ChatGptApi
//
//    init {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://api.openai.com/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(OkHttpClient.Builder().addInterceptor { chain ->
//                val original = chain.request()
//                val request = original.newBuilder()
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer $apiKey")
//                    .method(original.method, original.body)
//                    .build()
//                chain.proceed(request)
//            }.build())
//            .build()
//
//        chatGptApi = retrofit.create(ChatGptApi::class.java)
//    }
//
//    suspend fun getChatCompletion(prompt: String): String {
//        val requestBody = ChatRequest(prompt)
//        val response = chatGptApi.getChatCompletion(requestBody)
//        Log.d("ChatGptApiClient", "getChatCompletion: ${response.code()}, ${response.raw().request.url}")
//        Log.d("ChatGptApiClient", "getChatCompletion: ${response.body()}")
//        return response.body()?.choices?.get(0)?.message?.content ?: "No response"
//    }
//}

class ChatGptApiClient(apiKey: String) {

    private val chatGptApi: ChatGptApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(apiKeyInterceptor(apiKey)).build())
            .build()

        chatGptApi = retrofit.create(ChatGptApi::class.java)
    }

    suspend fun getChatCompletion(prompt: String): String {
        val requestBody = ChatRequest(prompt)
        val response = chatGptApi.getChatCompletion(requestBody)
        Log.d("ChatGptApiClient", "getChatCompletion: ${response.code()}, ${response.raw().request.url}")
        Log.d("ChatGptApiClient", "getChatCompletion: ${response.body()}")
        return response.body()?.choices?.get(0)?.message?.content ?: "No response"
    }

    private fun apiKeyInterceptor(apiKey: String): Interceptor {
        return Interceptor { chain ->
            Log.d("apiCheck", apiKey)
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }
}