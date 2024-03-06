package com.example.villainlp.novel.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.shared.AlertCancelText
import com.example.villainlp.shared.AlertConfirmText
import com.example.villainlp.shared.AlertText
import com.example.villainlp.shared.AlertTitle
import com.example.villainlp.shared.TopBarTitleText
import com.example.villainlp.ui.theme.Primary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// LibraryScreen, CommentScreen, MyNovelScreen, ReadMyNovelScreen
@Composable
fun AlertPopup(
    title: String, // Alert에 Title에 해당
    message: String, // 만약 TextField가 있다면 TextField에 들어갈 메세지
    onDismiss: () -> Unit, // dismiss 동작
    onConfirm: () -> Unit, // 확인 동작
    novelTitle: String = "", // 책제목 또는 title 이외의 제목
    warningMessage: String = "", // TextField가 있을때 본문내용
    hasTextField: Boolean = false, // TextField 유무
    tfLabel: String = "", // TextField 라벨
    onTextFieldValueChange: (String) -> Unit = {}, // textField onChange
    confirmButtonText: String = "확인", // 확인버튼 텍스트
    dismissButtonText: String = "취소", // 취소버튼 텍스트
) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { AlertTitle(title) }
        },
        text = {
            if (hasTextField) {
                Column {
                    Text(
                        text = "\"${novelTitle}\"$warningMessage",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.yeongdeok_sea)),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { onTextFieldValueChange(it) },
                        modifier = Modifier
                            .width(300.dp)
                            .height(80.dp)
                            .padding(8.dp),
                        label = { Text(text = tfLabel) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Primary
                        ),
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) { AlertText(message) }
            }
        },
        confirmButton = {
            IconButton(
                onClick = { onConfirm() }
            ) { AlertConfirmText(confirmButtonText) }
        },
        dismissButton = {
            IconButton(
                onClick = { onDismiss() }
            ) { AlertCancelText(dismissButtonText) }
        },
        modifier = Modifier.padding(16.dp)
    )
}

// LibraryScreen, ReadBookScreen
fun formatRating(rating: Float): Float {
    val formattedString = String.format("%.2f", rating)
    return formattedString.toFloat()
}

// ReadMyNovelScreen, ReadBookScreen
@Composable
fun ReadScreenTopBar(
    title: String,
    navController: NavHostController,
    onClicked: () -> Unit = {},
    hasIcon: Boolean = false,
) {
    val isDarkTheme = isSystemInDarkTheme()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 4.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "back")
            }
            TopBarTitleText(title, isDarkTheme)
            if (hasIcon) {
                IconButton(onClick = { onClicked() }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "upload")
                }
            } else {
                Spacer(modifier = Modifier.size(15.dp))
            }
        }
        Divider(color = if (isDarkTheme) Color.LightGray else Color.LightGray)
    }
}

// MyNovel, Library (Card에 화살표 이미지)
@Composable
fun FrontArrowImage() {
    Image(
        modifier = Modifier
            .size(33.dp)
            .padding(5.dp),
        painter = painterResource(id = R.drawable.arrow_right),
        contentDescription = "Front Arrow"
    )
}

// MyNovel, Library (제목 Text 속성)
@Composable
fun TitleText(title: String) {
    Text(
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp),
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight(600),
        color = Color.DarkGray,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// MyNovel, Library (요약 Text 속성)
@Composable
fun DescriptionText(description: String) {
    Text(
        modifier = Modifier
            .wrapContentWidth()
            .height(45.dp),
        text = description,

        fontSize = 14.sp,
        fontWeight = FontWeight(500),
        color = Color.DarkGray,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

// MyNovel, Library (저자 Text 속성)
@Composable
fun AuthorText(author: String) {
    Text(
        text = author,

        fontSize = 12.sp,
        fontWeight = FontWeight(500),
        textAlign = TextAlign.Start,
        color = Color.DarkGray,
    )
}

// 스와이프 가능한 박스(MyNovel, Comment, Library)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableBox(
    modifier: Modifier,
    onConfirmValueChange: (SwipeToDismissBoxValue) -> Boolean,
    backgroundContent: @Composable (Color) -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState( // SwipeToDismissBoxState로 변경됨
        positionalThreshold = { it * 0.50f },
        confirmValueChange = { onConfirmValueChange(it) }
    )

    val color by animateColorAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Primary, // DismissValue 변경
        label = "ColorAnimation"
    )

    SwipeToDismissBox( // SwipeToDismiss -> SwipeToDismissBox로 변경
        modifier = modifier,
        state = dismissState,
        enableDismissFromEndToStart = true, // direction이 사라지고 Dismiss를 어떻게 할지 불린값으로 조절, EndToStart == 끝이 시작(우->좌)
        enableDismissFromStartToEnd = false, // StartToEnd == (좌->우) 이건 막아둠(SwipeToDismissBoxState가 Confrim하려면 EndToStart여야 하기 때문
        backgroundContent = { backgroundContent(color) },
        content = { content() }
    )
}

// 스와이프 삭제(Library, MyNovel, ChatList)
@Composable
fun DeleteNovelCard(color: Color, text: String, cardHeight: Dp, imageVector: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // LazyColumn의 넓이에 맞춤
            .height(cardHeight),
        colors = CardDefaults.cardColors(containerColor = color), // 50%넘게 스와이프하면 색이 바뀜
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), // Card 크기에 맞춤
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(end = 15.dp),
                    text = text,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White,
                    ),
                )
                Icon(
                    modifier = Modifier.padding(end = 15.dp),
                    imageVector = imageVector,
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    }
}


// 스와이프시 삭제(Library, MyNovel)
@OptIn(ExperimentalMaterial3Api::class)
fun deleteContents(
    it: SwipeToDismissBoxValue,
    onConfirmValueChanged: () -> Unit,
): Boolean {
    if (it == SwipeToDismissBoxValue.EndToStart) { // DismissValue는 사용 불가 -> SwipeToDismissBoxValue로 변경, ToStart는 EndToStart와 동일
        CoroutineScope(Dispatchers.Main).launch {
            onConfirmValueChanged()
            delay(200)
        }
    }
    return true
}
