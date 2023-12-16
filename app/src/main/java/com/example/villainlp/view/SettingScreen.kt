package com.example.villainlp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SettingScreen(navController: NavHostController, signOutClicked: () -> Unit){
    MyScaffold("설정", navController) { LogoutButton { signOutClicked() } }
}

@Composable
fun UserProfile() {
    // 유저 프로필 화면
}

@Composable
fun LogoutButton(signOutClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { signOutClicked() }) {
            Text(text = "LogOut")
        }
    }
}


