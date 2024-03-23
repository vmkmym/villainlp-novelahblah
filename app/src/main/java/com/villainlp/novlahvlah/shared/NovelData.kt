package com.villainlp.novlahvlah.shared

// Chat, GeminiChat, MyNovel(Model,view,viewModel), FirebaseTools 에서 사용중
data class RelayNovel(
    val title: String = "",
    val author: String = "",
    val script: String = "",
    val userID: String = "",
    val rating: Float = 0.0f,
    val createdDate: String = "",
    val documentID: String? = null,
)

// ChatList(Model, Screen, viewModel), CreateGround, FirebaseTools에서 사용중
data class NovelInfo(
    val title: String = "",
    val assistId: String = "",
    val threadId: String = "",
    val userID: String = "",
    val createdDate: String = "",
    val documentID: String? = null,
    val uuid: String = ""
)