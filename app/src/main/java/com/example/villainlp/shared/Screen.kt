package com.example.villainlp.shared

sealed class Screen(val route: String) {
    data object Profile : Screen("UserProfileScreen")
    data object Lottie : Screen("LottieScreen")
    data object Login : Screen("LoginScreen")
    data object Chatting : Screen("ChattingScreen/{title}/{threadId}/{assistantKey}")
    data object CreativeYard : Screen("CreativeYardScreen")
    data object Library : Screen("LibraryScreen")
    data object MyBook : Screen("MyBookScreen")
    data object ReadMyBook : Screen("ReadMyBookScreen/{title}/{script}")
    data object ReadLibraryBook : Screen("ReadLibraryBookScreen/{title}/{script}/{documentId}/{views}")
    data object Rating : Screen("RatingScreen/{documentId}")
    data object ChattingList : Screen("ChattingScreen")
    data object Comment : Screen("CommentScreen/{documentId}")
    data object GeminiChat : Screen("GeminiChatScreen/{title}/{uuid}")

    //회원가입 화면
    data object SignUp : Screen("SignUpScreen")
    data object Report : Screen("Report/{blackedID}/{blackedName}")
    data object BlockManage : Screen("BlockScreen")

}