package com.example.villainlp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.villainlp.chat.geminiChat.GeminiChatScreen
import com.example.villainlp.chat.openAichat.ChatListScreen
import com.example.villainlp.chat.openAichat.ChatScreen
import com.example.villainlp.novel.library.comment.CommentScreen
import com.example.villainlp.chat.ground.CreativeYardScreen
import com.example.villainlp.novel.library.LibraryScreen
import com.example.villainlp.socialLogin.LoginScreen
import com.example.villainlp.socialLogin.LottieScreen
import com.example.villainlp.novel.myNovel.MyBookScreen
import com.example.villainlp.novel.library.rating.RatingScreen
import com.example.villainlp.novel.library.ReadLibraryBookScreen
import com.example.villainlp.novel.myNovel.readMyNovel.ReadMyBookScreen
import com.example.villainlp.setting.SettingScreen
import com.example.villainlp.shared.Screen
import com.example.villainlp.socialLogin.SignUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VillainNavigation(
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit,
    navController: NavHostController,
    auth: FirebaseAuth,
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        // 초기화면
        composable(Screen.Lottie.route) { LottieScreen(navController, auth) }

        // 로그인화면
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }

        // 창작마당
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController, auth) }

        // 릴레이소설이 채팅리스트화면
        composable(Screen.ChattingList.route) { ChatListScreen(navController, auth) }

        // 채팅 화면
        composable(Screen.Chatting.route) {
            val title = it.arguments?.getString("title") ?: "title"
            val threadId = it.arguments?.getString("threadId") ?: "threadId"
            val assistantKey = it.arguments?.getString("assistantKey") ?: "assistantKey"
            ChatScreen(navController, auth, title, threadId, assistantKey)
        }

        // gemini chat
        composable(Screen.GeminiChat.route) {
            val title = it.arguments?.getString("title") ?: "title"
            GeminiChatScreen(navController, auth, title)
        }

        // 별점화면
        composable(Screen.Rating.route) {
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            RatingScreen(navController, documentId)
        }

        // 내서재화면
        composable(Screen.MyBook.route) { MyBookScreen(navController, auth) }

        // 내서재에서 저장한 소설 눌렀을 때 화면
        composable(Screen.ReadMyBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val script = it.arguments?.getString("script")?: "script"
            ReadMyBookScreen(navController, title, script)
        }

        // 도서관화면
        composable(Screen.Library.route) { LibraryScreen(navController, auth) }

        // 도서관에서 저장한 소설 눌렀을 때 화면
        composable(Screen.ReadLibraryBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val script = it.arguments?.getString("script")?: "script"
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            val views = it.arguments?.getString("views")?: "views"
            ReadLibraryBookScreen(navController, title, script, documentId, views)
        }

        composable(Screen.Comment.route){
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            CommentScreen(navController, auth, documentId)
        }

        // 설정-프로필화면
        composable(Screen.Profile.route) { SettingScreen(auth, navController, signOutClicked) }

        // 회원가입화면
        composable(Screen.SignUp.route) { SignUpScreen(navController, auth) }
    }
}

