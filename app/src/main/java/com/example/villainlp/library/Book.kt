package com.example.villainlp.library

data class Book(
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val script: String = "",
    val userID: String = "",
    val rating: Float = 0.0f,
    val views: Int = 0,
    val starCount: Int = 0,
    val totalRate: Float = 0.0f,
    val uploadDate: String = "",
    val commentCount: Int = 0,
    val documentID: String? = null,
)

data class Comment(
    val author: String = "",
    val uploadDate: String = "",
    val script: String = "",
    val userID: String = "",
    val documentID: String? = null
)