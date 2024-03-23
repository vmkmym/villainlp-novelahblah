package com.villainlp.novlahvlah.socialLogin

import com.villainlp.novlahvlah.chat.openAichat.TextContent

enum class StartUiState(val content: TextContent) {
    TEXT1(TextContent(
        "당신만의 소설을 AI와 함께 \n작성해보세요 :)", "사용자와 AI가 함께 손을 잡고 창의적인 소설 여행을 \n" +
                "할 수 있는 소설 계주 서비스를 제공합니다.\n이 앱을 통해 사용자가 각자의 상상력과 창의력을 자유롭게 펼칠 수 있으며 AI와 협업하는 독특한 경험을 할 수 있습니다."
    )),
    TEXT2(TextContent(
        "소설 장르를 골라서\n소설을 작성할 수 있습니다.",
        "사용자의 소설 이야기 전개에 이어서 AI가 이야기를 쓰고, AI의 이야기 내용에 따라 사용자가 소설 이야기를 진행하는 것을 반복하여 사용자가 한 권의 소설책을 창작하는 것을 도와줍니다."
    )),
    TEXT3(TextContent(
        "여러분을 환상적인 이야기의 세계로 초대합니다.",
        "사용자가 쓴 첫 장은 곧바로 소설의 시작 지점이 됩니다. 계주처럼 사용자가 AI에게 바톤을 터치하면 AI는 사용자가 만든 이야기에 이어서 새로운 전개를 진행합니다. 사용자는 AI의 글을 받아들이거나 수정하여 이야기를 계속 발전시킬 수 있습니다. "
    ))
}