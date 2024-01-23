@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.villainlp.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserProfileScreen(
    viewModel: SettingViewModel,
    auth: FirebaseAuth,
    navController: NavHostController
) {
    val settingUiState = SettingUiState(viewModel)
    settingUiState.fetchUserData(auth)

    val url = "https://github.com/KDT-villainlp/villainlp" // 임시로 깃허브 주소 넣어둠
    val context = LocalContext.current

    settingUiState.MyScaffoldProfile(
        title = "설정",
        navController = navController
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(vertical = 30.dp))

            // Display user profile image
            settingUiState.DisplayUserProfileImage(viewModel.userImage.value)

            Spacer(modifier = Modifier.padding(vertical = 20.dp))

            // Display user fields
            settingUiState.DisplayUserFields(
                userName = viewModel.userName.value ?: "",
                onUserNameChange = {},
                userEmail = viewModel.userEmail.value ?: "",
                onUserEmailChange = {}
            )

            // Display sign out button
            settingUiState.DisplaySignOutButton { viewModel.signOut(auth) }
            // Display customer inquiry
            settingUiState.DisplayCustomerInquiry(url, context)
        }
    }
}



