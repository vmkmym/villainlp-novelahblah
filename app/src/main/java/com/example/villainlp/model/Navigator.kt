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
import com.example.villainlp.view.SettingScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VillainNavigation(
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit,
    navController: NavHostController,
    auth: FirebaseAuth,
) {
    NavHost(navController = navController, startDestination = Screen.Lottie.route) {
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }
        composable(Screen.Library.route) { LibraryScreen(navController, auth) }
        composable(Screen.MyBook.route) { MyBookScreen(navController, auth) }
        composable(Screen.Settings.route) { SettingScreen(navController) { signOutClicked() } }
        composable(Screen.ChattingList.route) { ChattingListScreen(navController, auth) }
        composable(Screen.Lottie.route) { LottieScreen(navController, auth) }
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController, auth) }
        composable(Screen.Chatting.route) {
            val title = it.arguments?.getString("title")?: "title"
            val threadId = it.arguments?.getString("threadId")?: "threadId"
            val assistantKey = it.arguments?.getString("assistantKey")?: "assistantKey"
            ChattingScreen(navController, auth, title, threadId, assistantKey)
        }
        composable(Screen.Rating.route) {
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            RatingScreen(navController, documentId)
        }
        composable(Screen.ReadMyBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val script = it.arguments?.getString("script")?: "script"
            ReadMyBookScreen(navController, title, script, auth)
        }
        composable(Screen.ReadLibraryBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val script = it.arguments?.getString("script")?: "script"
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            ReadLibraryBookScreen(navController, title, script, documentId)
        }

    }
}

