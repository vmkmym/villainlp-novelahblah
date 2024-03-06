package com.example.villainlp.chat.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.villainlp.R
import com.example.villainlp.ui.theme.Primary

// CreateGround, ChatScreen, GeminiScreen
@Composable
fun AlertConfirmText() {
    Text(
        text = "확인",
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    )
}

// CreateGround, ChatScreen, GeminiScreen
@Composable
fun AlertCancelText() {
    Text(
        text = "취소",
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    )
}

// Gemini, Chat
@Composable
fun AlertTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}

// Gemini, Chat
@Composable
fun AlertText() {
    Text(
        text = "작성한 소설을 저장하시겠습니까?",
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 15.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}