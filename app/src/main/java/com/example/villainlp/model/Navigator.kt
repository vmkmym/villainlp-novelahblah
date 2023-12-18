package com.example.villainlp.model

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.villainlp.view.CreativeYardScreen
import com.example.villainlp.view.ChattingScreen
import com.example.villainlp.view.LibraryScreen
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.MyBookScreen
import com.example.villainlp.view.ChattingListScreen
import com.example.villainlp.view.RatingScreen
import com.example.villainlp.view.ReadMyBookScreen
import com.example.villainlp.view.LottieScreen
import com.example.villainlp.view.ReadLibraryBookScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.villainlp.view.UserProfileScreen

@Composable
fun VillainNavigation(
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit,
    navController: NavHostController,
    auth: FirebaseAuth,
) {
    NavHost(navController = navController, startDestination = Screen.CreativeYard.route) {
        // 초기화면
        composable(Screen.Lottie.route) { LottieScreen(navController, auth) }

        // 로그인화면
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }

        // 창작마당
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController, auth) }

        // 릴레이소설이 채팅리스트화면
        composable(Screen.ChattingList.route) { ChattingListScreen(navController, auth) }

        // 채팅 화면
        // TODO: 채팅 할 때 말풍선 패딩 주기, 내가 채팅 길게 해도 다 보이게 하기(옵션)
        composable(Screen.Chatting.route) {
            val title = it.arguments?.getString("title")?: "title"
            val threadId = it.arguments?.getString("threadId")?: "threadId"
            val assistantKey = it.arguments?.getString("assistantKey")?: "assistantKey"
            ChattingScreen(navController, auth, title, threadId, assistantKey)
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
            ReadMyBookScreen(navController, title, script, auth)
        }

        // 도서관화면
        composable(Screen.Library.route) { LibraryScreen(navController, auth) }

        // 도서관에서 저장한 소설 눌렀을 때 화면
        composable(Screen.ReadLibraryBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val script = it.arguments?.getString("script")?: "script"
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            ReadLibraryBookScreen(navController, title, script, documentId)
        }

        // 설정-프로필화면
        composable(Screen.Profile.route) {
            UserProfileScreen(auth, signOutClicked = { signOutClicked()}, navController)
        }
    }
}

