package com.example.villainlp.novel

import java.util.UUID

// ChattingList, CreateGround에서 사용중
data class NovelInfo(
    val title: String = "",
    val assistId: String = "",
    val threadId: String = "",
    val userID: String = "",
    val createdDate: String = "",
    val documentID: String? = null,
    val uuid: String = UUID.randomUUID().toString()
)
