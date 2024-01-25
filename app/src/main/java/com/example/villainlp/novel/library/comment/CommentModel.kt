package com.example.villainlp.novel.library.comment

data class Comment(
    val author: String = "",
    val uploadDate: String = "",
    val script: String = "",
    val userID: String = "",
    val documentID: String? = null
)