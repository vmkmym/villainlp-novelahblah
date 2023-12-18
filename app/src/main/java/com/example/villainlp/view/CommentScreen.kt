package com.example.villainlp.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.model.Comment
import com.example.villainlp.model.FirebaseTools
import com.example.villainlp.model.FirebaseTools.uploadComment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentScreen(navController: NavHostController, auth: FirebaseAuth, documentId: String) {
    var comment by remember { mutableStateOf("") }
    val user = auth.currentUser
    val scope = rememberCoroutineScope()
    var commentList by remember { mutableStateOf<List<Comment>>(emptyList()) }
    val reloadLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.reload))
    var isAnimationPlaying by remember { mutableStateOf(false) } // 애니메이션 재생 상태 추적
    var commentDocumentId by remember { mutableStateOf("") }
    var commentCount by remember { mutableStateOf(0) }

    val keyboardController = LocalSoftwareKeyboardController.current // 키보드 컨트롤러 가져오기

    var showDialog by remember { mutableStateOf(false) }
    val firePuppleLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_pupple))

    LaunchedEffect(Unit) {
        commentList = FirebaseTools.fetchCommentsFromFirestore(documentId)
        commentCount = commentList.size
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                )
                {
                    Row {
                        Image(
                            modifier = Modifier
                                .clickable { navController.popBackStack() }
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.arrow_left),
                            contentDescription = "back"
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Text(text = "댓글")
                        Text(text = "$commentCount")
                    }
                    LottieAnimation(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                isAnimationPlaying = !isAnimationPlaying
                                scope.launch {
                                    commentList =
                                        FirebaseTools.fetchCommentsFromFirestore(documentId)
                                    commentCount = commentList.size
                                }
                            }, // 클릭 이벤트에서 애니메이션 재생
                        composition = reloadLottie,
                        isPlaying = isAnimationPlaying, // 재생 상태에 따라 애니메이션 재생
                        iterations = 1 // 재생 상태에 따라 애니메이션 재생 횟수 설정
                    )
                }
                Divider(color = Color(0xFF9E9E9E))
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    modifier = Modifier
                        .width(300.dp),
                    value = comment,
                    onValueChange = { comment = it },
                    singleLine = false
                )
                Spacer(modifier = Modifier.size(20.dp))
                Column(
                    modifier = Modifier
                        .clickable {
                            val currentDate =
                                SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm",
                                    Locale.getDefault()
                                ).format(
                                    Date()
                                )
                            val myComent = Comment(
                                author = user?.displayName ?: "ERROR",
                                uploadDate = currentDate,
                                script = comment,
                                userID = user?.uid ?: "ERROR"
                            )
                            uploadComment(documentId, myComent)
                            scope.launch {
                                commentList =
                                    FirebaseTools.fetchCommentsFromFirestore(documentId)
                                commentCount = commentList.size
                            }
                            comment = ""
                            keyboardController?.hide() // 키보드 내리기
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "등록")
                }
            }
        }
    )
    {
        Comments(it, commentList, auth) { comment ->
            commentDocumentId = comment.documentID ?: "ERROR"
            showDialog = true
        }
    }
    if (showDialog) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = { showDialog = false },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                    Text(text = "댓글을 삭제하시겠습니까?")
                    LottieAnimation(
                        modifier = Modifier.size(40.dp),
                        composition = firePuppleLottie,
                        iterations = LottieConstants.IterateForever
                    )
                }
            },
            confirmButton = {
                IconButton(
                    onClick = {
                        FirebaseTools.deleteCommentFromFirestore(documentId, commentDocumentId)
                        scope.launch {
                            commentCount = commentList.size
                            commentList = FirebaseTools.fetchCommentsFromFirestore(documentId)
                            FirebaseTools.updateCommentCount(documentId, commentList.size)
                        }
                        showDialog = false
                    }
                ) { Text(text = "확인") }
            },
            dismissButton = {
                IconButton(
                    onClick = { showDialog = false }
                ) {
                    Text(text = "취소")
                }
            },
            modifier = Modifier
                .padding(16.dp)
        )
    }
}


@Composable
fun Comments(
    paddingValues: PaddingValues,
    commentList: List<Comment>,
    auth: FirebaseAuth,
    onClicked: (Comment) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(commentList) { comments ->
            CommentBox(comments, auth) { comment ->
                onClicked(comment)
            }
        }
    }
}


@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun CommentBox(
    comment: Comment,
    auth: FirebaseAuth,
    onClicked: (Comment) -> Unit,
) {
    val user = auth.currentUser
    val isCurrentUser = (user?.uid == comment.userID)

    val swipeableState = rememberSwipeableState(initialValue = 0f)

    val swipeableModifier = Modifier.swipeable(
        state = swipeableState,
        anchors = mapOf(0f to 0f, -150f to -150f), // Define the anchors
        orientation = Orientation.Horizontal,
        thresholds = { _, _ -> FractionalThreshold(0.1f) },
        resistance = null
    )

    // Define the AnimatedVisibility for the image
    val imageVisibility = swipeableState.offset.value <= -150f

    val fireLottie by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.fire_red))

    if (isCurrentUser) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(
                            swipeableState.offset.value.roundToInt(),
                            0
                        )
                    } // Apply the offset
                    .then(swipeableModifier) // Apply the swipeable modifier
            ) {
                Row {
                    Text(text = comment.author)
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(text = comment.uploadDate)
                }
                Text(text = comment.script)
                Divider()
            }
            // The image that appears when swiped
            AnimatedVisibility(visible = imageVisibility) {
                Column(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                680 - swipeableState.offset.value.roundToInt(),
                                0
                            )
                        } // Use the same offset as the Card
                        .size(50.dp, 100.dp)
                        .border(
                            1.dp, Color(0xFFF5F5F5),
                            RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onClicked(comment) },
                        composition = fireLottie,
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(text = comment.author)
                Spacer(modifier = Modifier.size(20.dp))
                Text(text = comment.uploadDate)
            }
            Text(text = comment.script)
            Divider()
        }
    }
}