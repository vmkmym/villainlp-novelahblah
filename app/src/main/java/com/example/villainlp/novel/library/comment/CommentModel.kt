package com.example.villainlp.novel.library.comment

data class Comment(
    val author: String = "",
    val uploadDate: String = "",
    val script: String = "",
    val userID: String = "",
    val documentID: String? = null
)

enum class BottomText(val text: String){
    On("코멘트를 작성해주세요 :)"),
    Off("주제와 무관한 내용 및 악플은 삭제될 수 있습니다."),
    Submit("등록")
}

enum class LimitChar(var n: Int){
    Max(500)
}