package com.example.villainlp.novel

import androidx.compose.ui.Modifier
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.SwipeableState

data class SwipeableParameters @OptIn(ExperimentalWearMaterialApi::class) constructor(
    val swipeableState: SwipeableState<Float>,
    val swipeableModifier: Modifier,
    val imageVisibility: Boolean,
)

enum class TopBarTitle(val title: String){
    MyNovel("내서재"),
    Library("도서관"),
    Comment("댓글")
}

enum class DeleteAlert(val text: String){
    CommonTitle("정말로 삭제하시겠습니까?"),
    LibraryMessage("선택한 소설이 삭제됩니다."),
    MyNovelMessage("내 작업 공간에서 선택한 소설이 삭제가 됩니다."),
    CommentTitle("댓글을 삭제하시겠습니까?"),
    CommentMessage("주제와 무관한 내용 및 악플은 삭제하는게 좋습니다")
}