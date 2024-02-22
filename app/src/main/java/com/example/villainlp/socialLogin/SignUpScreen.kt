package com.example.villainlp.socialLogin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.shared.Screen
import com.example.villainlp.ui.theme.Primary
import com.google.firebase.auth.FirebaseAuth


// TODO: 회원가입 화면, Firebase와 연동하여 회원가입 기능 구현
@Composable
fun SignUpScreen(navController: NavHostController, auth: FirebaseAuth) {
    var idValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    val helloLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.hello))
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Primary else Color.Black

    val windowHeight = LocalConfiguration.current.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = windowHeight * 0.1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // 애니메이션
        LottieAnimation(
            composition = helloLottie,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            iterations = LottieConstants.IterateForever
        )

        // 헤더
        Text(
            text = "노블라블라 서비스 이용을 위한 회원가입을 해주세요",
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE),
                letterSpacing = 0.28.sp,
            )
        )

        CustomOutlinedTextField(
            value = idValue,
            onValueChange = { newValue ->
                idValue = newValue
                // 여기에 아이디 입력 값 변경 시 수행할 작업 추가
            },
            label = "아이디를 입력하세요."
        )

        CustomOutlinedTextField(
            value = pwValue,
            onValueChange = { newValue ->
                pwValue = newValue
                // 여기에 비밀번호 입력 값 변경 시 수행할 작업 추가
            },
            label = "비밀번호를 입력하세요."
        )

        // 회원가입
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 10.dp,
                    spotColor = Color(0x5917C3CE),
                    ambientColor = Color(0x5917C3CE)
                )
                .width(320.dp)
                .height(60.dp)
                .background(color = Color(0xFF17C3CE), shape = RoundedCornerShape(size = 17.dp))
                .clickable {
                    navController.navigate(Screen.Login.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign up",
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
}