package com.example.villainlp.novel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun DeleteAlert(
    title: String,
    warningMessage: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))
    AlertDialog(
        icon = {
            LottieAnimation(
                modifier = Modifier.size(40.dp),
                composition = firePuppleLottie,
                iterations = LottieConstants.IterateForever
            )
        },
        containerColor = Color.White,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            )
        },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = warningMessage,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            IconButton(
                onClick = { onConfirm() }
            ) {
                Text(
                    text = "확인",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
        },
        dismissButton = {
            IconButton(
                onClick = { onDismiss() }
            ) {
                Text(
                    text = "취소",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

// LibraryScreen, ReadBookScreen
fun formatRating(rating: Float): Float {
    val formattedString = String.format("%.2f", rating)
    return formattedString.toFloat()
}