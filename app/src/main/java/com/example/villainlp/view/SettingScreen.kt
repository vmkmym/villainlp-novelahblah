@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.TextContent
import com.example.villainlp.ui.theme.Blue789

@Composable
fun LottieScreen(navController: NavController) {
    StartLottie(navController)
}

@Composable
private fun StartLottie(navController: NavController) {
    // 애니메이션 시작 화면
    val robotlottie by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.robot)
    )
    var index by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Blue789)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                LottieAnimation(
                    composition = robotlottie,
                    modifier = Modifier.fillMaxWidth(),
                    iterations = LottieConstants.IterateForever
                )
            }
            Box(
                modifier = Modifier.padding(top = 320.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image99),
                    contentDescription = "그림자 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(start = 60.dp, end = 25.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Descriptions(textList[index])
    }
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.progress22),
            contentDescription = "다음 화면으로 이동 버튼",
            modifier = Modifier
                .padding(bottom = 40.dp, end = 30.dp)
                .clickable {
                    // 이미지 클릭 시 인덱스 증가
                    index = (index + 1) % textList.size

                    // 인덱스가 2일 때, 로그인 화면으로 이동
                    if (index == 3) {
                        navController.navigate("LoginScreen")
                    }

                },
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun LoginScreen(signInClicked: () -> Unit) {
    var idValue by remember { mutableStateOf("") }
    var pwValue by remember { mutableStateOf("") }
    val hellolottie by rememberLottieComposition(
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
            composition = hellolottie,
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
        CreateLoginFields (
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
private fun StartLottie() {
    // 애니메이션 시작 화면
    val robotlottie by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.robot)
    )
    var index by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Blue789)
    ) {
        Box {
            Row(
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                LottieAnimation(
                    composition = robotlottie,
                    modifier = Modifier.fillMaxWidth(),
                    iterations = LottieConstants.IterateForever
                )
            }
            Box(
                modifier = Modifier.padding(top = 320.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image99),
                    contentDescription = "그림자 이미지",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(start = 60.dp, end = 25.dp),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
        Descriptions(textList[index])
    }
    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.progress22),
            contentDescription = "다음 화면으로 이동 버튼",
            modifier = Modifier
                .padding(bottom = 40.dp, end = 30.dp)
                .clickable {
                    // 이미지 클릭 시 인덱스 증가
                    index = (index + 1) % textList.size
                },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun Descriptions(content: TextContent) {
    Column(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 60.dp)
    ) {
        Text(
            text = content.subtitle,
            modifier = Modifier
                .width(378.dp)
                .height(80.dp),
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight(900),
                color = Color(0xFFFFFFFF),
            )
        )
        Text(
            text = content.body,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF),
            )
        )
    }
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

val textList = listOf(
    TextContent(
        "당신만의 소설을 AI와 함께 \n작성해보세요 :)", "사용자와 AI가 함께 손을 잡고 창의적인 소설 여행을 \n" +
                "할 수 있는 소설 계주 서비스를 제공합니다.\n이 앱을 통해 사용자가 각자의 상상력과 창의력을 자유롭게 펼칠 수 있으며 AI와 협업하는 독특한 경험을 할 수 있습니다."
    ),
    TextContent(
        "소설 장르를 골라서\n소설을 작성할 수 있습니다.",
        "사용자의 소설 이야기 전개에 이어서 AI가 이야기를 쓰고, AI의 이야기 내용에 따라 사용자가 소설 이야기를 진행하는 것을 반복하여 사용자가 한 권의 소설책을 창작하는 것을 도와줍니다."
    ),
    TextContent(
        "여러분을 환상적인 이야기의 세계로 초대합니다.",
        "사용자가 쓴 첫 장은 곧바로 소설의 시작 지점이 됩니다. 계주처럼 사용자가 AI에게 바톤을 터치하면 AI는 사용자가 만든 이야기에 이어서 새로운 전개를 진행합니다. 사용자는 AI의 글을 받아들이거나 수정하여 이야기를 계속 발전시킬 수 있습니다. "
    )
)

@Composable
fun CreateLoginFields(
    idValue: String,
    onIdValueChange: (String) -> Unit,
    pwValue: String,
    onPwValueChange: (String) -> Unit
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
