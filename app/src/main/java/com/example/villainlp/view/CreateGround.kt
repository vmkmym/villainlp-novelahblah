@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.FirebaseTools.saveNovelInfo
import com.example.villainlp.model.NovelInfo
import com.example.villainlp.model.Screen
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreativeYardScreen(navController: NavHostController, auth: FirebaseAuth) {
    MyScaffold("창작마당", navController) { Creative(it, navController, auth) }
}

@Composable
fun Creative(modifier: Modifier, navController: NavHostController, auth: FirebaseAuth) {
    Box(
        modifier = modifier.padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryDefalutButton()
                }
                items(genreHappy.size) { index ->
                    CategoryButton(genreHappy[index])
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genreSad.size) { index ->
                    CategoryButton(genreSad[index])
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(genreScary.size) { index ->
                    CategoryButton(genreScary[index])
                }
            }
            LazyColumn {
                item {
                    Header()
                    CreativeYard(navController, auth)
                }
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .padding(start = 12.dp, top = 20.dp, end = 16.dp, bottom = 12.dp)
    ) {
        Text(
            text = "✏️ 당신만의 소설을 써보세요",
            style = TextStyle(
                fontSize = 22.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF000000)
            )
        )
    }
}

@OptIn(BetaOpenAI::class)
@Composable
fun CreativeYard(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var assistantId by remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val openAI by lazy { OpenAI(token) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }
    val scope = rememberCoroutineScope()
    var alertMessage by remember { mutableStateOf("") }
    val user = auth.currentUser
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CreativeCard(
                cardColor = Color(0xFFC3FCD9),
                cardTitle = "작가의 마당",
                cardDescription = "스토리라인이 있는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_1),
                onCardClick = {
                    showDialog = true
                    assistantId = getString(context, R.string.assistant_key_for_novelist)
                    alertMessage = "작가의 마당"
                }
            )
            CreativeCard(
                cardColor = Color(0xFFFFEAEA),
                cardTitle = "꿈의 마당",
                cardDescription = "스토리라인이 없는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_2),
                onCardClick = {
                    showDialog = true
                    assistantId = getString(context, R.string.assistant_key_for_general)
                    alertMessage = "꿈의 마당"
                }
            )
        }
        CreativeCard(
            cardColor = Color(0xFFFFE8C6),
            cardTitle = "아이디어 마당",
            cardDescription = "아이디어를 나누어봐요",
            imageResource = painterResource(id = R.drawable.idea),
            onCardClick = {
                showDialog = true
                assistantId = getString(context, R.string.assistant_key_for_general)
                alertMessage = "아이디어 마당"
            }
        )
    }
    if (showDialog) {
        // 다이얼로그 강조를 위한 스크림 오버레이
        ScrimOverlay(
            onClick = { showDialog = false },
            visible = showDialog,
            scrimColor = Color.Black.copy(alpha = 0.5f)
        )

        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = firePuppleLottie,
                    iterations = LottieConstants.IterateForever
                )
            },
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "$alertMessage 채팅방을 생성하시겠습니까?",
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
                    OutlinedTextField(
                        value = dialogTitle,
                        onValueChange = { dialogTitle = it },
                        modifier = Modifier
                            .width(300.dp)
                            .height(80.dp)
                            .padding(8.dp),
                        label = {
                            Text(
                                text = "채팅방 이름을 입력해주세요.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Blue789,
                            unfocusedBorderColor = Blue789
                        ),
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        scope.launch {
                            val thread = openAI.thread()
                            threadId = thread.id
                            val currentDate =
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                                    Date()
                                )
                            val novelInfo =
                                NovelInfo(
                                    title = dialogTitle,
                                    assistId = assistantId,
                                    threadId = extractThreadId(threadId.toString()),
                                    userID = user!!.uid,
                                    createdDate = currentDate
                                )
                            saveNovelInfo(novelInfo)
                        }
                        showDialog = false
                        navController.navigate(Screen.ChattingList.route)
                    }
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
                    onClick = { showDialog = false }
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
            containerColor = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeCard(
    cardColor: Color,
    cardTitle: String,
    cardDescription: String,
    imageResource: Painter,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(elevation = 8.dp, spotColor = cardColor, ambientColor = cardColor)
            .width(160.dp)
            .height(226.dp),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = imageResource,
                contentDescription = "Card Image",
                modifier = Modifier
                    .size(66.dp)
                    .offset(
                        x = 16.dp,
                        y = 20.dp
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .offset(
                        x = 16.dp,
                        y = 100.dp
                    )
            ) {
                Text(
                    text = cardTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cardDescription,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@Composable
fun CategoryButton(genre: String) {
    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = Color(0xFF17C3CE),
                shape = RoundedCornerShape(size = 17.dp)
            )
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Text(
            text = genre,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE)
            )
        )
    }
}

@Composable
fun CategoryDefalutButton() {
    Box(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .background(color = Color(0xFF17C3CE), shape = RoundedCornerShape(size = 17.dp))
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Text(
            text = "All",
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF)
            )
        )
    }
}


val genreHappy = listOf(
    "Fantasy", "Romance", "Comedy"
)

val genreScary = listOf(
    "Horror", "Thriller", "Mystery", "SF"
)

val genreSad = listOf(
    "Melodrama", "Tragedy", "Family", "others"
)

fun extractThreadId(threadIdString: String): String {
    return threadIdString.substringAfter("ThreadId(id=").substringBeforeLast(")")
}


@Composable
fun ScrimOverlay(
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(alpha = 0.5f),
    visible: Boolean = true,
    onClick: () -> Unit = {},
) {
    if (visible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(scrimColor)
                .clickable(onClick = onClick)
        )
    }
}

