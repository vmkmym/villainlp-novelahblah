package com.example.villainlp.view

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.TextContent
import com.example.villainlp.ui.theme.Blue789

@Composable
fun LottieScreen(navController: NavController) {
    StartLottie()
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
        LottieAnimation(
            composition = robotlottie,
            iterations = LottieConstants.IterateForever
        )
        Image(
            painter = painterResource(id = R.drawable.image99),
            contentDescription = "그림자 이미지",
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.weight(1f))
        Descriptions(textList[index])
        Spacer(modifier = Modifier.weight(5f))
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


@Preview
@Composable
fun LottieScreenPreview() {
    LottieScreen(navController = rememberNavController())
}


@Composable
fun UserProfile() {
    // 유저 프로필 화면
}


@Composable
fun LoginScreen(signInClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_login),
            contentDescription = "구글로그인",
            modifier = Modifier
                .size(250.dp, 100.dp)
                .clickable { signInClicked() }
        )
    }
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