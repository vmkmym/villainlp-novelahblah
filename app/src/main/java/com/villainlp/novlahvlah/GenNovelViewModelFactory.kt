package com.villainlp.novlahvlah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.villainlp.novlahvlah.chat.openAichat.ChatListViewModel
import com.villainlp.novlahvlah.chat.openAichat.ChatModel
import com.villainlp.novlahvlah.chat.openAichat.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.villainlp.novlahvlah.setting.SettingViewModel

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