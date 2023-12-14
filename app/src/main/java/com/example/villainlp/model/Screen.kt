package com.example.villainlp.model

sealed class Screen(val route: String) {
    object Login : Screen("LoginScreen")
    object Home : Screen("HomeScreen")
//    object Logout : Screen("LogoutScreen")
    object Library : Screen("LibraryScreen")
    object MyBook : Screen("MyBookScreen")
    object Settings : Screen("SettingScreen")
    object ReadBook : Screen("ReadBookScreen/{title}/{description}")

    // Test 용
    object TestSendBookData : Screen("book")
}