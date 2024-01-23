package com.example.villainlp.socialLogin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.ui.theme.Blue789

@Composable
fun LoginScreen(signInClicked: () -> Unit) {
    var idValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    val helloLottie by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.hello)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // 애니메이션
        LottieAnimation(
            composition = helloLottie,
            modifier = Modifier
                .height(200.dp)
                .width(200.dp),
            iterations = LottieConstants.IterateForever
        )

        // 헤더
        Text(
            text = "노블라블라 서비스 이용을 위한 로그인을 해주세요",
//            modifier = Modifier.padding(bottom = 44.dp),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE),
                letterSpacing = 0.28.sp,
            )
        )

        // 아이디, 비번 입력
        CreateLoginFields(
            idValue = idValue,
            onIdValueChange = { newValue ->
                idValue = newValue
                // 여기에 아이디 입력 값 변경 시 수행할 작업 추가
            },
            pwValue = pwValue,
            onPwValueChange = { newValue ->
                pwValue = newValue
                // 여기에 비밀번호 입력 값 변경 시 수행할 작업 추가
            }
        )

        // check box and Remember me
        Row(
            modifier = Modifier
                .width(320.dp)
                .padding(end = 150.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = Color(0xFF17C3CE),
                        shape = RoundedCornerShape(size = 3.dp)
                    )
                    .width(20.dp)
                    .height(20.dp)
            ) {
            }
            Text(
                text = "Remember me",
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 30.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(600),
                    color = Color(0xFF3A3A3A),
                    letterSpacing = 0.28.sp,
                )
            )
        }

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
                .padding(vertical = 14.dp)
                .clickable { signInClicked() }
        ) {
            Text(
                text = "Sign in",
                modifier = Modifier
                    .padding(start = 120.dp, end = 100.dp),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                    letterSpacing = 0.48.sp,
                )
            )
        }

        // 비밀번호를 잊었나요?
        Text(
            text = "비밀번호를 잊었나요?",
            modifier = Modifier.padding(top = 16.dp, bottom = 30.dp),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE),
                letterSpacing = 0.28.sp,
            )
        )

        // 가입하신 계정이 없나요? 가입하세요
        Row {
            Text(
                text = "가입하신 계정이 없나요?",
                modifier = Modifier.padding(end = 12.dp),
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF000000),
                    letterSpacing = 0.28.sp,
                )
            )
            Text(
                text = "Sign up",
                modifier = Modifier.padding(bottom = 34.dp),
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
            text = "--------- or continue with ---------",
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
                .size(250.dp, 100.dp)
                .clickable { signInClicked() }
        )
    }
}

@Composable
fun CreateLoginFields(
    idValue: String,
    onIdValueChange: (String) -> Unit,
    pwValue: String,
    onPwValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = idValue,
        onValueChange = onIdValueChange,
        modifier = Modifier
            .width(320.dp)
            .height(80.dp)
            .padding(8.dp), // 추가적으로 padding 적용 가능
        label = { Text("아이디를 입력하세요.") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue789,
            unfocusedBorderColor = Blue789
        )
    )
    OutlinedTextField(
        value = pwValue,
        onValueChange = onPwValueChange,
        modifier = Modifier
            .width(320.dp)
            .height(80.dp)
            .padding(8.dp), // 추가적으로 padding 적용 가능
        label = { Text("비밀번호를 입력하세요.") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue789,
            unfocusedBorderColor = Blue789
        )
    )
}
