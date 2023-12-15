package com.example.villainlp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.villainlp.view.CreativeYardScreen
import com.example.villainlp.view.HomeScreen
import com.example.villainlp.view.LibraryScreen
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.MyBookScreen
import com.example.villainlp.view.RatingScreen
import com.example.villainlp.view.ReadBookScreen
import com.example.villainlp.view.SaveNovelButton
import com.example.villainlp.view.SettingScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun VillainNavigation(
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit,
    user: FirebaseUser?,
    navController: NavHostController
) {
    val startDestination = remember { if (user == null) { Screen.Login.route } else { Screen.Home.route } }
    NavHost(navController = navController, startDestination = "TestScreenRate") {
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }
        composable(Screen.Home.route) { HomeScreen(navController, user) }
        composable(Screen.Library.route) { LibraryScreen(navController) }
        composable(Screen.MyBook.route) { MyBookScreen(user, navController) }
        composable(Screen.Settings.route) { SettingScreen { signOutClicked() } }
        composable(Screen.Rating.route) {
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            RatingScreen(navController, documentId)
        }
        composable(Screen.ReadBook.route) {
            val title = it.arguments?.getString("title")?: "title"
            val description = it.arguments?.getString("description")?: "description"
            val documentId = it.arguments?.getString("documentId")?: "documentId"
            ReadBookScreen(navController, title, description,documentId)
        }
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController) }

        // Test 용도
        composable("TestScreenRate") { SaveNovelButton(navController, user) }
    }
}

