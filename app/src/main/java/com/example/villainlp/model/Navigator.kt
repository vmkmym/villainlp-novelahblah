package com.example.villainlp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.villainlp.model.Screen.Logout
import com.example.villainlp.view.CreativeYard
import com.example.villainlp.view.BookScreen
import com.example.villainlp.view.CreativeYardScreen
import com.example.villainlp.view.HomeScreen
import com.example.villainlp.view.Logout
import com.example.villainlp.view.LoginScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.villainlp.view.RatingScreen
import com.example.villainlp.view.SavedBooks
import com.google.firebase.auth.FirebaseUser

@Composable
fun VillainNavigation(
    signInClicked: () -> Unit,
    signOutClicked: () -> Unit,
    user: FirebaseUser?,
    navController: NavHostController,
    firebaseAuth: FirebaseAuth
) {
    val startDestination = remember {
        if (user == null) {
            Screen.Login.route
        } else {
            Screen.CreativeYard.route
        }
    }
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }
        composable(Screen.Home.route) { HomeScreen(navController, firebaseAuth = firebaseAuth) } // 창작마당
        composable(Logout.route) { Logout { signOutClicked() } } // 설정 스크린
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController) }
        composable(Screen.Library.route) { SavedBooks(user) }

        // Test 용도
        composable(Screen.TestSendBookData.route) { BookScreen(navController, user) }
        composable("TestScreenRate") { RatingScreen(navController) }
        composable("Test2") { RatingScreen(navController) }
//        composable("Test3") { MyScaffold() }
    }
}

