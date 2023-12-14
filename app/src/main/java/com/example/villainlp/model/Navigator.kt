package com.example.villainlp.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.villainlp.model.Screen.Logout
import com.example.villainlp.view.CreativeYardScreen
import com.example.villainlp.view.HomeScreen
import com.example.villainlp.view.LoginScreen
import com.example.villainlp.view.Logout
import com.google.firebase.auth.FirebaseAuth
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
            Screen.Home.route
        } else {
            Screen.Home.route
        }
    }
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) { LoginScreen(signInClicked = { signInClicked() }) }
        composable(Screen.Home.route) { HomeScreen(navController, firebaseAuth = firebaseAuth) } // 창작마당
        composable(Logout.route) { Logout { signOutClicked() } } // 설정 스크린
        composable(Screen.CreativeYard.route) { CreativeYardScreen(navController) }
    }
}


