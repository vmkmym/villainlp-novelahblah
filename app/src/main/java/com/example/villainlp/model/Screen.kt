package com.example.villainlp.model

sealed class Screen(val route: String) {
    object Screen1 : Screen("screen1")
    object Screen2 : Screen("screen2")
    object ChatbotScreen : Screen("ChatScreen")
}