package com.example.villainlp.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.villainlp.R
import com.example.villainlp.shared.MyScaffoldBottomBar
import com.example.villainlp.shared.MyScaffoldTopBar
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth

class SettingUiState(private val viewModel: SettingViewModel) {
    // 사용자 정보 가져오기
    fun fetchUserData(auth: FirebaseAuth) {
        viewModel.fetchUserData(auth)
    }

    // 사용자 프로필 사진
    @Composable
    fun DisplayUserProfileImage(userImage: Uri?) {
        userImage?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "구글 계정 프로필 사진",
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
                .height(60.dp)
                .background(color = Color(0xFF17C3CE), shape = RoundedCornerShape(size = 17.dp))
                .padding(vertical = 14.dp)
                .clickable { signOutClicked() }
        ) {
            Text(
                text = "Sign out",
                modifier = Modifier
                    .padding(
                        start = 120.dp,
                        end = 100.dp
                    ),
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
        Box(
            modifier = Modifier
                .clickable {
                    isClicked = true
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
                .padding(top = 70.dp)
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.notion1),
                    contentDescription = "Notion Logo Image"
                )
                // 고객 문의 텍스트
                Text(
                    text = "고객 문의 및 FAQ",
                    modifier = Modifier
                        .padding(start = 3.dp, end = 12.dp, bottom = 30.dp),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                        letterSpacing = 0.28.sp,
                    )
                )
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
        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            modifier = Modifier
                .width(320.dp)
                .height(80.dp)
                .padding(8.dp),
            label = { Text("이름") },
            singleLine = true,
            enabled = false, // 편집 불가능하게 설정
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue789,
                unfocusedBorderColor = Blue789
            )
        )
        OutlinedTextField(
            value = userEmail,
            onValueChange = onUserEmailChange,
            modifier = Modifier
                .width(320.dp)
                .height(80.dp)
                .padding(8.dp),
            label = { Text("이메일") },
            singleLine = true,
            enabled = false, // 편집 불가능하게 설정
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Blue789,
                unfocusedBorderColor = Blue789
            )
        )
    }

    // Scaffold
    @Composable
    fun MyScaffoldProfile(
        title: String,
        navController: NavHostController,
        content: @Composable (Modifier) -> Unit,
    ) {
        Scaffold(
            topBar = {
                MyScaffoldTopBar(title)
            },
            bottomBar = {
                MyScaffoldBottomBar(navController)
            }
        ) {
            content(
                Modifier
                    .padding(it)
            )
        }
    }
}