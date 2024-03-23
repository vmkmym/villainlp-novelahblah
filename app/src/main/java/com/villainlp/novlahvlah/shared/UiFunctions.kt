package com.villainlp.novlahvlah.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.villainlp.novlahvlah.ui.theme.Primary
import com.villainlp.novlahvlah.R

// CreateGround, ChatListScreen, MyNovelScreen
@Composable
fun MyScaffold(
    title: String,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = { MyScaffoldTopBar(title) },
        bottomBar = { MyScaffoldBottomBar(navController) }
    ) {
        content(
            Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Transparent),
        )
    }
}

// SettingScreen, MyScaffold(UiFunctions)
@Composable
fun MyScaffoldBottomBar(navController: NavHostController) {
    val currentScreen = remember { mutableStateOf(navController.currentDestination?.route) }

    Column {
        Divider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
        Row(
            Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Transparent)
                .padding(start = 33.dp, top = 16.dp, end = 33.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            // Child views.
            CustomIconButton(
                defaultIcon = R.drawable.home,
                clickedIcon = R.drawable.home_clicked,
                isCurrentScreen = currentScreen.value == Screen.CreativeYard.route,
                iconText = "창작마당",
                clickedTextColor = Primary,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.CreativeYard.route) }
            CustomIconButton(
                defaultIcon = R.drawable.forum,
                clickedIcon = R.drawable.forum_clicked,
                isCurrentScreen = currentScreen.value == Screen.ChattingList.route,
                iconText = "릴레이소설",
                clickedTextColor = Primary,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.ChattingList.route) }
            CustomIconButton(
                defaultIcon = R.drawable.book_5,
                clickedIcon = R.drawable.book_clicked,
                isCurrentScreen = currentScreen.value == Screen.MyBook.route,
                iconText = "내서재",
                clickedTextColor = Primary,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.MyBook.route) }
            CustomIconButton(
                defaultIcon = R.drawable.local_library,
                clickedIcon = R.drawable.local_library_clicked,
                isCurrentScreen = currentScreen.value == Screen.Library.route,
                iconText = "도서관",
                clickedTextColor = Primary,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.Library.route) }
            CustomIconButton(
                defaultIcon = R.drawable.settings,
                clickedIcon = R.drawable.settings_clicked,
                isCurrentScreen = currentScreen.value == Screen.Profile.route,
                iconText = "설정",
                clickedTextColor = Primary,
                defaultTextColor = Color(0xFFbbbbbb)
            ) { navController.navigate(Screen.Profile.route) }
        }
    }
}

// SettingScreen, MyScaffold(UiFunctions)
@Composable
fun MyScaffoldTopBar(title: String) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopBarTitleText(title, isDarkTheme)
        }
        Divider(color = if (isDarkTheme) Color.LightGray else Color.LightGray)
    }
}


// MyScaffoldBottomBar랑 연결
@Composable
fun CustomIconButton(
    defaultIcon: Int,
    clickedIcon: Int,
    isCurrentScreen: Boolean,
    iconText: String,
    clickedTextColor: Color,
    defaultTextColor: Color,
    onClicked: () -> Unit,
) {
    val icon = if (isCurrentScreen) clickedIcon else defaultIcon
    val textColor = if (isCurrentScreen) clickedTextColor else defaultTextColor

    Column(
        modifier = Modifier.clickable { onClicked() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(0.dp)
                .width(28.dp)
                .height(28.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.height(17.dp),
            text = iconText,
            fontSize = 10.sp,
            fontWeight = FontWeight(500),
            color = textColor,
        )
    }
}

// MyScaffoldTopBar, CommonUI(ReadScreenTopBar)
@Composable
fun TopBarTitleText(title: String, isDarkTheme: Boolean) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight(600),
        color = if (isDarkTheme) Color.White else Color.DarkGray
    )
}

// CreateGround, ChatScreen, GeminiScreen, ReadMyNovel
@Composable
fun AlertConfirmText(confirmText: String = "확인") {
    Text(
        text = confirmText,
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    )
}

// CreateGround, ChatScreen, GeminiScreen, ReadMyNovel
@Composable
fun AlertCancelText(dismissText: String = "취소") {
    Text(
        text = dismissText,
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    )
}

// Gemini, Chat, CommonUI(Novel)
@Composable
fun AlertTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}

// Gemini, Chat, CommonUI(Novel)
@Composable
fun AlertText(message: String = "작성한 소설을 저장하시겠습니까?") {
    Text(
        text = message,
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}

// Report, BlockManage
@Composable
fun DefaultTopBar(title: String, navController: NavHostController, isDarkTheme: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(start = 4.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "back")
            }
            TopBarTitleText(title, isDarkTheme)
            Spacer(modifier = Modifier.size(15.dp))
        }
        Divider(color = if (isDarkTheme) Color.LightGray else Color.LightGray)
    }
}