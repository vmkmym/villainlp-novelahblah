package com.example.villainlp.model

sealed class Screen(val route: String) {
    object Login : Screen("LoginScreen")
    object Home : Screen("HomeScreen/{title}/{threadId}/{assistantKey}")
    object CreativeYard : Screen("CreativeYardScreen")
    object Library : Screen("LibraryScreen")
    object MyBook : Screen("MyBookScreen")
    object Settings : Screen("SettingScreen")
    object ReadBook : Screen("ReadBookScreen/{title}/{description}/{documentId}")
    object Rating : Screen("RatingScreen/{documentId}")
    object ChattingList : Screen("ChattingScreen")

    // Test 용
    object TestSendBookData : Screen("book")
}