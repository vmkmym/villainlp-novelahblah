package com.example.villainlp.novel.myNovel

import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.SwipeableState

//data class RelayChatToNovel(
//    val title: String = "",
//    val author: String = "",
//    val script: String = "",
//    val userID: String = "",
//    val rating: Float = 0.0f,
//    val createdDate: String = "",
//    val documentID: String? = null,
//)

data class SwipeableParameters @OptIn(ExperimentalWearMaterialApi::class) constructor(
    val swipeableState: SwipeableState<Float>,
    val swipeableModifier: Modifier,
    val imageVisibility: Boolean,
)