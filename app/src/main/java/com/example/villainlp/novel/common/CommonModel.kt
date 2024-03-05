package com.example.villainlp.novel.common

enum class TopBarTitle(val title: String){
    MyNovel("내서재"),
    Library("도서관"),
    Comment("댓글")
}

// LibraryScreen, ReadMyBookScreen에서 사용중
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