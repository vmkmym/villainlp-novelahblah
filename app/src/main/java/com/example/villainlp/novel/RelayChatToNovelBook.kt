package com.example.villainlp.novel

data class RelayChatToNovelBook(
    val title: String = "",
    val author: String = "",
    val script: String = "",
    val userID: String = "",
    val rating: Float = 0.0f,
    val createdDate: String = "",
    val documentID: String? = null,
)
