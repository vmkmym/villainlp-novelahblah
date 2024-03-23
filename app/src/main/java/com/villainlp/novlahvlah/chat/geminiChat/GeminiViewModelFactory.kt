package com.villainlp.novlahvlah.chat.geminiChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.villainlp.novlahvlah.BuildConfig

val GeminiViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        return with(modelClass) {
            when {
                isAssignableFrom(GeminiChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model for chat
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = generationConfig {
                            generationConfig {
                                temperature = 0.9f // Higher temperature results in more random outputs
                                topK = 1 // Top-k nucleus sampling
                                topP = 1f // Top-p nucleus sampling
                                maxOutputTokens = 1500 // Maximum number of tokens to generate
                            }
                        },
                        safetySettings = listOf(
                            SafetySetting(
                                HarmCategory.HARASSMENT,
                                BlockThreshold.MEDIUM_AND_ABOVE
                            ), // 괴롭힘 차단
                            SafetySetting(
                                HarmCategory.HATE_SPEECH,
                                BlockThreshold.MEDIUM_AND_ABOVE
                            ), // 혐오스러운 콘텐츠 차단
                            SafetySetting(
                                HarmCategory.SEXUALLY_EXPLICIT,
                                BlockThreshold.MEDIUM_AND_ABOVE
                            ), // 성적으로 은유된 콘텐츠 차단
                            SafetySetting(
                                HarmCategory.DANGEROUS_CONTENT,
                                BlockThreshold.MEDIUM_AND_ABOVE
                            ), // 위험한 콘텐츠 차단
                        )
                    )
                    GeminiChatViewModel(generativeModel, model = GeminiChatModel())
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}