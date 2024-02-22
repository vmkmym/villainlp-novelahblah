package com.example.villainlp.socialLogin

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.ui.theme.Primary


@Composable
fun LoginScreen(signInClicked: () -> Unit) {
    var idValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    val helloLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.hello))
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Primary else Color.Black

    // 현재 화면의 크기를 가져옵니다.
    val windowWidth = LocalConfiguration.current.screenWidthDp.dp
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
            text = "노블라블라 서비스 이용을 위한 로그인을 해주세요",
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
                // TODO: 여기에 아이디 입력 값 변경 시 수행할 작업 추가
            },
            label = "아이디를 입력하세요."
        )
        CustomOutlinedTextField(
            value = pwValue,
            onValueChange = { newValue ->
                pwValue = newValue
                // TODO: 여기에 비밀번호 입력 값 변경 시 수행할 작업 추가
            },
            label = "비밀번호를 입력하세요."
        )

        // 로그인
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
                .clickable { signInClicked() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Sign in",
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

        // TODO: 비밀번호를 잊었을 때 재설정하는 화면으로 이동하는 기능 추가
        Text(
            text = "비밀번호를 잊었나요?",
            modifier = Modifier
                .padding(
                    top = windowWidth * 0.02f, // fraction을 사용하여 padding 값을 설정합니다.
                    start = windowWidth * 0.3f, // fraction을 사용하여 padding 값을 설정합니다.
                    end = windowWidth * 0.3f // fraction을 사용하여 padding 값을 설정합니다.
                ),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE),
                letterSpacing = 0.28.sp,
            )
        )

        // TODO: 회원가입 화면으로 이동하는 기능 추가
        Row {
            Text(
                text = "가입하신 계정이 없나요?",
                modifier = Modifier.padding(
                    top = windowHeight * 0.02f,
                    end = windowWidth * 0.02f
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(500),
                    color = textColor,
                    letterSpacing = 0.28.sp,
                )
            )
            Text(
                text = "Sign up",
                modifier = Modifier.padding(
                    top = windowHeight * 0.02f
                ),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF17C3CE),
                    letterSpacing = 0.28.sp,
                )
            )
        }
        // 소셜 로그인 (구글)
        Text(
            text = "or continue with",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    top = windowHeight * 0.02f,
                    bottom = windowHeight * 0.02f),
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF9EAFB0),
                letterSpacing = 0.32.sp,
            )
        )
        Image(
            painter = painterResource(id = R.drawable.google_login),
            contentDescription = "구글 로그인",
            modifier = Modifier
                .padding(top = windowHeight * 0.04f)
                .fillMaxWidth(0.3f)
                .aspectRatio(1f)
                .clickable { signInClicked()}
        )
    }
}


@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(320.dp)
            .height(80.dp)
            .padding(8.dp),
        label = { Text(label) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Primary
        )
    )
}