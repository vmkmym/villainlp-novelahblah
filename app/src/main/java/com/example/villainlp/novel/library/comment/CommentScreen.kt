package com.example.villainlp.novel.library.comment

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.novel.common.SwipeableBox
import com.example.villainlp.novel.common.TopBarTitle
import com.example.villainlp.novel.common.deleteContents
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    documentId: String,
    viewModel: CommentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    // VM에서 관리할 변수들 UI들과 분리되어 관리할 대상
    val commentList by viewModel.commentList.collectAsState()
    val comment by viewModel.commentText.collectAsState()
    val isAnimationPlaying by viewModel.isAnimationPlaying.collectAsState()

    // TextField 포커스 여부를 체크하는 것들, UI와 분리 X
    var isTextFieldFocused by remember { mutableStateOf(false) }
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current // 키보드 컨트롤러 가져오기

    // Comment들을 불러옴
    viewModel.loadComments(documentId)

    Scaffold(
        topBar = {
            CommentTopBar(
                navController = navController,
                viewModel = viewModel,
                isPlaying = isAnimationPlaying,
                onClick = { viewModel.onReloadClicked(documentId) }
            )
        },
        bottomBar = {
            CommentBottomBar(
                comment = comment,
                focusRequester = focusRequester,
                maxCharacterCount = LimitChar.Max.n,
                isTextFieldFocused = isTextFieldFocused,
                viewModel = viewModel,
                onFocusChanged = { isTextFieldFocused = it.isFocused },
                onCommentChanged = { if (it.length <= LimitChar.Max.n) viewModel.onCommentChanged(it) },
                onPlaceholder = { if (!isTextFieldFocused) FocuseText(BottomText.On.text) else FocuseText(BottomText.Off.text) },
                onSubmitClicked = {
                    viewModel.onCommentSubmit(documentId)
                    keyboardController?.hide()
                    isTextFieldFocused = false
                }
            )
        }
    )
    {
        Comments(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .addFocusCleaner(focusManager), //Textfield 포커스를 위한 모디파이어
            auth = auth,
            documentId = documentId,
            commentList = commentList,
            viewModel = viewModel
        )
    }
}

// TopBar UI 구조
@Composable
fun CommentTopBar(
    navController: NavController,
    viewModel: CommentViewModel,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 4.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "back")
                }
                Spacer(modifier = Modifier.size(20.dp))
                TopBarText(viewModel)
            }
            ReloadingAnimation(
                isPlaying = isPlaying,
                onClick = { onClick() }
            )
        }
        Divider(color = Color(0xFF9E9E9E))
    }
}

// 댓글(수)로 구성된 Text
@Composable
fun TopBarText(
    viewModel: CommentViewModel,
) {
    val commentCount by viewModel.commentCount.collectAsState()

    Text(text = TopBarTitle.Comment.title)
    Text(text = "$commentCount")
}

// 새로고침 버튼
@Composable
fun ReloadingAnimation(
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val color = if (isSystemInDarkTheme()) R.raw.reload_white else R.raw.reload_black
    val reloadLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(color))

    LottieAnimation(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() }, // 클릭 이벤트에서 애니메이션 재생
        composition = reloadLottie,
        isPlaying = isPlaying, // 재생 상태에 따라 애니메이션 재생
        iterations = 1 // 재생 상태에 따라 애니메이션 재생 횟수 설정
    )
}

// Bottom Bay 구성
@Composable
fun CommentBottomBar(
    comment: String,
    focusRequester: FocusRequester,
    maxCharacterCount: Int,
    isTextFieldFocused: Boolean,
    viewModel: CommentViewModel,
    onFocusChanged: (FocusState) -> Unit,
    onCommentChanged: (String) -> Unit,
    onPlaceholder: @Composable () -> Unit,
    onSubmitClicked: () -> Unit,
) {
    Column {
        Divider(thickness = 0.5.dp, color = Color(0xFF9E9E9E))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TextFieldColumn(
                    comment = comment,
                    focusRequester = focusRequester,
                    maxCharacterCount = maxCharacterCount,
                    isTextFieldFocused = isTextFieldFocused,
                    onFocusChanged = { onFocusChanged(it) },
                    onCommentChanged = { onCommentChanged(it) },
                    onPlaceholder = { onPlaceholder() }
                )
            }
            SubmitButton(
                viewModel = viewModel,
                onClick = { onSubmitClicked() }
            )
        }
    }
}

// 글씨를 쓰는 TextField의 구성
@Composable
fun TextFieldColumn(
    comment: String,
    focusRequester: FocusRequester,
    maxCharacterCount: Int,
    isTextFieldFocused: Boolean,
    onFocusChanged: (FocusState) -> Unit,
    onCommentChanged: (String) -> Unit,
    onPlaceholder: @Composable () -> Unit,
) {
    CommentTextField(
        comment = comment,
        focusRequester = focusRequester,
        onFocusChanged = { onFocusChanged(it) },
        onCommentChanged = { onCommentChanged(it) },
        onPlaceholder = { onPlaceholder() }
    )
    if (isTextFieldFocused) {
        CommentLength(comment, maxCharacterCount)
    }
}

// 댓글을 다는 TextField 속성
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentTextField(
    comment: String,
    focusRequester: FocusRequester,
    onFocusChanged: (FocusState) -> Unit,
    onCommentChanged: (String) -> Unit,
    onPlaceholder: @Composable () -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // 높이를 내부 내용에 맞게 자동 조정
            .focusRequester(focusRequester = focusRequester)
            .onFocusChanged { onFocusChanged(it) },
        value = comment,
        onValueChange = { onCommentChanged(it) },
        placeholder = { onPlaceholder() },
        singleLine = false,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

// Focuse에 따라 값이바뀌는 TextValue
@Composable
fun FocuseText(
    text: String,
) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFFBBBBBB)
        )
    )
}

// 글자수 제한
@Composable
fun CommentLength(
    comment: String,
    maxCharacterCount: Int,
) {
    Row {
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = "${comment.length}/${maxCharacterCount}",
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFBBBBBB)
            )
        )
    }
    Spacer(modifier = Modifier.size(12.dp))
}

// 등록 버튼
@Composable
fun SubmitButton(
    viewModel: CommentViewModel,
    onClick: () -> Unit
) {
    val commentText by viewModel.commentText.collectAsState()
    val textColor = if (commentText != "") MaterialTheme.colorScheme.onSurface else Color(0xFFBBBBBB)

    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent, // 버튼이 활성화 되어 있을 때 배경색
            disabledBackgroundColor = Color.Transparent // 비활성화 되어 있을 때 배경색
        ),
        enabled = commentText != ""
    ) {
        Text(
            text = BottomText.Submit.text,
            color = textColor // contentColor가 적용이 안돼서 따로 설정해둠...
        )
    }
}

// 댓글들의 구성
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Comments(
    modifier: Modifier,
    auth: FirebaseAuth,
    documentId: String,
    commentList: List<Comment>,
    viewModel: CommentViewModel,
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(commentList, key = { item -> item.documentID ?: "ERROR" }) { comment ->

            val user = auth.currentUser
            val isCurrentUser = user?.uid == comment.userID

            SwipeableBox(
                modifier = Modifier.animateItemPlacement(),
                onConfirmValueChange = {
                    if (isCurrentUser) {
                        deleteContents(it) { viewModel.deleteComment(documentId, comment) }
                    } else {
                        false
                    }
                },
                backgroundContent = { color ->
                    if (isCurrentUser) {
                        DeleteComment(
                            color = color,
                            text = "삭제하기",
                            imageVector = Icons.Default.Delete
                        )
                    } else {
                        DeleteComment(
                            color = Color.Red,
                            text = "타인의 코멘트는 삭제 할 수 없습니다.",
                            imageVector = Icons.Default.Lock
                        )
                    }
                },
                content = { MyCommentColumn(comment) })

        }
    }
}

// 내 글만 지우기 가능하게 MyComment로 구분
@Composable
fun MyCommentColumn(
    comment: Comment,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSystemInDarkTheme()) {
                    Color(0xFF181c1f)
                } else {
                    Color.White
                }
            )
            .padding(12.dp)
    ) {
        CommentInfoAndScript(comment)
    }
    Divider()
}

// 댓글 Text 구성
@Composable
fun CommentInfoAndScript(comment: Comment) {
    CommentInfo(comment)
    CommentScript(comment)
}

// 댓글 Text 속성
@Composable
fun CommentInfo(comment: Comment) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.author,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = comment.uploadDate,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFBBBBBB)
            )
        )
    }
}

// 댓글 Text 속성
@Composable
fun CommentScript(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = comment.script,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

// 모든 댓글을 보여줌(내것이 아닌것은 지울 수 없게)
@Composable
fun DeleteComment(color: Color, text: String, imageVector: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
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
    Divider()
}

// Textfield 포커스를 위한것
fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            doOnClear()
            focusManager.clearFocus()
        })
    }
}