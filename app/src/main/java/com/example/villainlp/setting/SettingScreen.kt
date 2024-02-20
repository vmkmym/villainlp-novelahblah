@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.villainlp.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.villainlp.GenNovelViewModelFactory
import com.example.villainlp.R
import com.example.villainlp.chat.openAichat.ChatModel
import com.example.villainlp.shared.MyScaffold
import com.example.villainlp.shared.SharedObjects.EMAIL
import com.example.villainlp.shared.SharedObjects.NAME
import com.example.villainlp.shared.SharedObjects.NOTION_INQUIRY
import com.example.villainlp.shared.SharedObjects.NOTION_LOGO
import com.example.villainlp.shared.SharedObjects.NOTION_URL
import com.example.villainlp.shared.SharedObjects.PROFILE_IMAGE
import com.example.villainlp.ui.theme.Primary
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingScreen(
    auth: FirebaseAuth,
    navController: NavHostController,
    signOutClicked: () -> Unit,
) {
    val url = NOTION_URL
    val context = LocalContext.current

    // ViewModel 인스턴스 생성
    val viewModel: SettingViewModel =
        viewModel(factory = GenNovelViewModelFactory(auth, ChatModel()))

    // StateFlow의 값을 가져오기 위해 collectAsState 함수를 사용합니다.
    val userImage by viewModel.userImage.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    // 현재 화면의 크기를 가져옵니다.
    val windowHeight = LocalConfiguration.current.screenHeightDp.dp
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White

    MyScaffold(
        title = "설정",
        navController = navController
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Spacer(modifier = Modifier.padding(vertical = windowHeight * 0.03f))
            // 로그인한 유저의 프로필 이미지를 가져옴
            DisplayUserProfileImage(userImage)
            Spacer(modifier = Modifier.padding(vertical = windowHeight * 0.02f))
            // 로그인한 유저의 이름, 이메일을 가져옴
            DisplayUserFields(
                userName = userName ?: "",
                onUserNameChange = {},
                userEmail = userEmail ?: "",
                onUserEmailChange = {}
            )
            Spacer(modifier = Modifier.padding(vertical = windowHeight * 0.01f))
            // 로그아웃 버튼
            DisplaySignOutButton { signOutClicked() }
            // 고객 문의 및 FAQ
            DisplayCustomerInquiry(url, context)
        }
    }
}


// 사용자 프로필 사진
@Composable
fun DisplayUserProfileImage(userImage: Uri?) {
    userImage?.let { imageUrl ->
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = PROFILE_IMAGE,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFF17C3CE), CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

// 로그아웃 버튼
@Composable
fun DisplaySignOutButton(signOutClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .shadow(
                elevation = 10.dp,
                spotColor = Color(0x5917C3CE),
                ambientColor = Color(0x5917C3CE)
            )
            .width(320.dp)
            .height(70.dp)
            .background(color = Color(0xFF17C3CE), shape = RoundedCornerShape(size = 17.dp))
            .padding(vertical = 14.dp)
            .clickable { signOutClicked() }
    ) {
        Text(
            text = "Sign out",
            modifier = Modifier
                .align(Alignment.Center),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFFFFFFF),
                letterSpacing = 0.48.sp,
            )
        )
    }
}

// 고객 문의 및 FAQ
@Composable
fun DisplayCustomerInquiry(url: String, context: Context) {
    var isClicked by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val windowSize = LocalConfiguration.current.screenWidthDp.dp // 화면의 너비를 가져옵니다.

    Spacer(modifier = Modifier.padding(top = windowSize * 0.07f))
    Box(
        modifier = Modifier
            .clickable {
                isClicked = true
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.notion1),
                contentDescription = NOTION_LOGO,
                modifier = Modifier.size(windowSize * 0.05f) // 없던 부분 추가
            )
            // 고객 문의 텍스트
            Text(
                text = NOTION_INQUIRY,
                modifier = Modifier
                    .padding(start = windowSize * 0.003f, end = windowSize * 0.012f),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight(500),
                    color = if (isDarkTheme) Color.White else Color.DarkGray,
                    letterSpacing = 0.28.sp,
                )
            )
            Spacer(modifier = Modifier.padding(bottom = windowSize * 0.03f))
        }
    }
}

// 사용자 이름, 이메일
@Composable
fun DisplayUserFields(
    userName: String,
    onUserNameChange: (String) -> Unit,
    userEmail: String,
    onUserEmailChange: (String) -> Unit,
) {
    val windowSize = LocalConfiguration.current.screenWidthDp.dp
    val isDarkTheme = isSystemInDarkTheme()

    OutlinedTextField(
        value = userName,
        onValueChange = onUserNameChange,
        modifier = Modifier
            .width(320.dp)
            .height(80.dp)
            .padding(8.dp),
        label = { Text(NAME) },
        singleLine = true,
        enabled = false, // 편집 불가능하게 설정
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = if (isDarkTheme) Color.White else Color.DarkGray,
            unfocusedBorderColor = Primary,
            disabledBorderColor = Primary,
            disabledLabelColor = Primary,
        )
    )
    OutlinedTextField(
        value = userEmail,
        onValueChange = onUserEmailChange,
        modifier = Modifier
            .width(320.dp)
            .height(80.dp)
            .padding(8.dp),
        label = { Text(EMAIL) },
        singleLine = true,
        enabled = false, // 편집 불가능하게 설정
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = if (isDarkTheme) Color.White else Color.DarkGray, // 텍스트 필드가 비활성화 상태일 때의 텍스트 색상
            unfocusedBorderColor = Primary, // 텍스트 필드에 포커스가 맞춰지지 않았을 때의 테두리 색상
            disabledBorderColor = Primary, // 텍스트 필드가 비활성화 상태일 때의 테두리 색상
            disabledLabelColor = Primary, // 텍스트 필드가 비활성화 상태일 때의 레이블 색상
        )
    )
}
