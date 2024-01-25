package com.example.villainlp.novel

// Chatting, MyNovel에서 사용중
data class RelayChatToNovelBook(
    val title: String = "",
    val author: String = "",
    val script: String = "",
    val userID: String = "",
    val rating: Float = 0.0f,
    val createdDate: String = "",
    val documentID: String? = null,
)
