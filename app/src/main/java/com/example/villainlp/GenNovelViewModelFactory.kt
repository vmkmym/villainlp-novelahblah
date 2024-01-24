package com.example.villainlp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.villainlp.chat.openAichat.ChatListViewModel
import com.example.villainlp.chat.openAichat.ChatModel
import com.example.villainlp.chat.openAichat.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.villainlp.setting.SettingViewModel

class GenNovelViewModelFactory(
    private val auth: FirebaseAuth, private val chatModel: ChatModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(auth) as T
            }
            modelClass.isAssignableFrom(ChatListViewModel::class.java) -> {
                ChatListViewModel(auth) as T
            }
            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(chatModel) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}