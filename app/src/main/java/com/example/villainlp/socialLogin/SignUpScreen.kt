package com.example.villainlp.socialLogin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.ui.theme.Primary

@Composable
fun SignUpScreen(
    navController: NavHostController,
    signUpClicked: (String, String) -> Unit,
) {
    var emailValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    val helloLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.hello))
    val windowHeight = LocalConfiguration.current.screenHeightDp.dp
    var showWarning by remember { mutableStateOf(false) }
    val showEmailInUseMessage by remember { mutableStateOf(false) }
    var showInvalidPasswordMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = windowHeight * 0.1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp)) // 간격 추가
                Message(message = "이전 화면으로 돌아가기")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
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
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(400),
                    color = Primary,
                    letterSpacing = 0.28.sp,
                )

                CustomOutlinedTextField(
                    value = emailValue,
                    onValueChange = { newValue ->
                        emailValue = newValue
                        // 이메일 형식 검사
                        val isEmailValid =
                            android.util.Patterns.EMAIL_ADDRESS.matcher(newValue).matches()
                        if (!isEmailValid) {
                            return@CustomOutlinedTextField
                        }
                    },
                    label = "이메일을 입력하세요.",
                )

                if (showEmailInUseMessage) {
                    Message("이미 사용 중인 이메일입니다.")
                }

                CustomOutlinedTextField(
                    value = pwValue,
                    onValueChange = { newValue ->
                        pwValue = newValue
                        // 비밀번호 형식 검사
                        val passwordPattern =
                            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!^&+=])(?=\\S+$).{10,}$".toRegex()
                        val isPasswordValid = passwordPattern.matches(newValue)
                        showInvalidPasswordMessage = !isPasswordValid
                    },
                    label = "비밀번호를 입력하세요."
                )

                if (showInvalidPasswordMessage) {
                    Message("특수문자 @#!\$%^&+=를 포함해주세요.\n10자 이상, 영문 대소문자, 숫자를 입력해주세요.")
                }

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
                        .background(
                            color = Color(0xFF17C3CE),
                            shape = RoundedCornerShape(size = 17.dp)
                        )
                        .clickable {
                            if (emailValue.isBlank() || pwValue.isBlank()) {
                                showWarning = true
                            } else {
                                signUpClicked(emailValue, pwValue)
                                navController.popBackStack()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign up",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 22.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        letterSpacing = 0.48.sp,
                    )
                }
                if (showWarning) {
                    Message("이메일 또는 비밀번호를 입력해주세요.")
                }
            }
        }
    }
}

@Composable
fun Message(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(10.dp),
        fontSize = 14.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight(400),
        color = Color(0xFFCE172C),
        letterSpacing = 0.28.sp,
    )
}