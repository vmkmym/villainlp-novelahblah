@file:OptIn(ExperimentalMaterial3Api::class)

package com.villainlp.novlahvlah.chat.ground

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.google.firebase.auth.FirebaseAuth
import com.villainlp.novlahvlah.R
import com.villainlp.novlahvlah.server.FirebaseTools.saveAtFirebase
import com.villainlp.novlahvlah.shared.AlertCancelText
import com.villainlp.novlahvlah.shared.AlertConfirmText
import com.villainlp.novlahvlah.shared.MyScaffold
import com.villainlp.novlahvlah.shared.NovelInfo
import com.villainlp.novlahvlah.shared.Screen
import com.villainlp.novlahvlah.ui.theme.LightBlack
import com.villainlp.novlahvlah.ui.theme.Primary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun CreativeYardScreen(navController: NavHostController, auth: FirebaseAuth) {
    MyScaffold("창작마당", navController) {
        Creative(it, navController, auth)
    }
}

@Composable
fun Creative(modifier: Modifier, navController: NavHostController, auth: FirebaseAuth) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(color = Color.Transparent),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderText()
        CreativeYard(navController, auth)
    }
}

@Composable
fun HeaderText() {
    val infiniteTransition = rememberInfiniteTransition(label = "소개문구 흘러가는 애니메이션")
    val offset by infiniteTransition // 무한 애니메이션 생성
        .animateFloat( // 텍스트의 오프셋을 애니메이션화
        initialValue = 1f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing), // 5초간 애니메이션
            repeatMode = RepeatMode.Restart
        ), label = "오프셋"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color = LightBlack, shape = RoundedCornerShape(size = 10.dp))
    ) {
        Text(
            text = "✏️ 당신만의 소설을 써보세요 ",
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.CenterStart)
                .offset(x = with(LocalDensity.current) { (offset * 1000).toDp() }),
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
                fontSize = 14.sp,
                fontWeight = FontWeight(500),
                color = Color.White,
                letterSpacing = 0.48.sp,
            )
        )
    }
}

@OptIn(BetaOpenAI::class)
@Composable
fun CreativeYard(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showGeminiDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var assistantId by remember { mutableStateOf("") }
    val token = getString(context, R.string.api_key)
    val openAI by lazy { OpenAI(token) }
    var threadId by remember { mutableStateOf<ThreadId?>(null) }
    val scope = rememberCoroutineScope()
    var alertMessage by remember { mutableStateOf("") }
    val user = auth.currentUser
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))

    Spacer(modifier = Modifier.padding(top = 12.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CreativeCard(
            cardColor = Color(0xFFC3FCD9),
            cardTitle = "작가의 마당",
            modelName = "model: ChatGPT-3.5",
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
            modelName = "model: ChatGPT-3.5",
            cardDescription = "스토리라인이 없는 경우",
            imageResource = painterResource(id = R.drawable.creative_yard_2),
            onCardClick = {
                showDialog = true
                assistantId = getString(context, R.string.assistant_key_for_general)
                alertMessage = "꿈의 마당"
            }
        )
        CreativeGeminiCard(
            cardColor = Color(0xFFC5F0E8),
            cardTitle = "꿈의 마당",
            modelName = "model: Gemini",
            cardDescription = "스토리라인이 없는 경우",
            imageResource = painterResource(id = R.drawable.gemini),
            onCardClick = {
                showGeminiDialog = true
                alertMessage = "꿈의 마당"
            }
        )
    }
    if (showDialog) {
        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = firePuppleLottie,
                    iterations = LottieConstants.IterateForever
                )
            },
            onDismissRequest = { showDialog = false },
            title = { AlertTitleText(alertMessage) },
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
                        textStyle = TextStyle(
                            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
                            color = Color.Black
                        ),
                        label = { AlertLabelText() },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Primary
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
                            saveAtFirebase(novelInfo)
                        }
                        showDialog = false
                        navController.navigate(Screen.ChattingList.route)
                    }
                ) { AlertConfirmText() }
            },
            dismissButton = {
                IconButton(
                    onClick = { showDialog = false }
                ) { AlertCancelText() }
            },
            containerColor = Color.White
        )
    }
    if (showGeminiDialog) {
        AlertDialog(
            icon = {
                LottieAnimation(
                    modifier = Modifier.size(40.dp),
                    composition = firePuppleLottie,
                    iterations = LottieConstants.IterateForever
                )
            },
            onDismissRequest = { showDialog = false },
            title = { AlertTitleText(alertMessage) },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = dialogTitle,
                        onValueChange = { dialogTitle = it },
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(80.dp)
                            .padding(8.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
                            color = Color.Black
                        ),
                        label = { AlertLabelText() },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Primary
                        ),
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        scope.launch {
                            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                            val currentUUID = UUID.randomUUID().toString() // 여기서 랜덤값 생성
                            val novelInfo =
                                NovelInfo(
                                    title = dialogTitle,
                                    userID = user!!.uid,
                                    createdDate = currentDate,
                                    uuid = currentUUID
                                )
                            saveAtFirebase(novelInfo)
                        }
                        showGeminiDialog = false
                        navController.navigate(Screen.ChattingList.route)
                    }
                ) { AlertConfirmText() }
            },
            dismissButton = {
                IconButton(
                    onClick = { showGeminiDialog = false }
                ) { AlertCancelText() }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun AlertTitleText(alertMessage: String) {
    Text(
        text = "$alertMessage 채팅방을 생성하시겠습니까?",
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}

@Composable
private fun AlertLabelText() {
    Text(
        text = "채팅방 이름을 입력해주세요.",
        style = TextStyle(
            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black
        )
    )
}


@Composable
fun CreativeCard(
    cardColor: Color,
    cardTitle: String,
    modelName: String,
    cardDescription: String,
    imageResource: Painter,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(elevation = 8.dp, spotColor = cardColor, ambientColor = cardColor)
            .fillMaxWidth(1f)
            .height(120.dp),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                Image(
                    painter = imageResource,
                    contentDescription = "Card Image",
                    modifier = Modifier
                        .size(80.dp)
                        .offset(x = 16.dp, y = 20.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .offset(x = 16.dp, y = 20.dp)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = cardTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = modelName,
                        fontSize = 10.sp,
                        color = Color.DarkGray
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
}

fun extractThreadId(threadIdString: String): String {
    return threadIdString
        .substringAfter("ThreadId(id=")
        .substringBeforeLast(")")
}

@Composable
fun CreativeGeminiCard(
    cardColor: Color,
    cardTitle: String,
    modelName: String,
    cardDescription: String,
    imageResource: Painter,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(elevation = 8.dp, spotColor = cardColor, ambientColor = cardColor)
            .fillMaxWidth(1f)
            .height(120.dp),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Row {
                Image(
                    painter = imageResource,
                    contentDescription = "Card Image",
                    modifier = Modifier
                        .size(80.dp)
                        .offset(x = 16.dp, y = 20.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .offset(x = 16.dp, y = 20.dp)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = cardTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = modelName,
                        fontSize = 10.sp,
                        color = Color.DarkGray
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
}