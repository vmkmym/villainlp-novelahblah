package com.example.villainlp.model

data class Book(
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val script: String = "",
    val userID: String = "",
    val rating: Float = 0.0f,
    val views: Int = 0,
    val uploadDate: String = "",
    val documentID: String? = null,
)
