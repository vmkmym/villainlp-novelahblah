package com.example.villainlp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.villainlp.view.BookScreen
import com.example.villainlp.view.HomeScreen
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.Logout
import com.example.villainlp.view.RatingBar
import com.example.villainlp.view.RatingScreen
import com.example.villainlp.view.SavedBooks
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
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Logout.route) { Logout { signOutClicked() } }
        composable(Screen.Library.route) { SavedBooks(user) }

        // Test 용도
        composable(Screen.TestSendBookData.route) { BookScreen(navController, user) }
        composable("TestScreenRate") { RatingScreen(navController) }
        composable("Test2") { RatingScreen(navController) }
    }
}

