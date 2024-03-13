package com.example.villainlp.novel.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.villainlp.shared.TopBarTitleText
import com.example.villainlp.ui.theme.Primary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ReportScreen(
    auth: FirebaseAuth,
    navController: NavHostController,
    blackID: String,
    blackedName: String
) {
    var selectedOption by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val isDarkTheme = isSystemInDarkTheme()
    val userID = auth.currentUser?.uid
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ReportTopBar(
                navController = navController,
                isDarkTheme = isDarkTheme
            )
        },
        bottomBar = {
            ReportBottomBar(
                isDarkTheme = isDarkTheme,
                navController = navController,
                modifier = Modifier
                    .clickable(enabled = selectedOption.isNotEmpty()) {
                        // 신고 내용 collections을 만들기
                        // DocumentID는 신고당한 userID
                        // 내부는 신고 라디오 목록을 카운트
                        // 기타는 기타내용이라는 collections를 만들기

                        // BlackList 만들기
                        // DocumentID는 신고하는 userID
                        // 필드값으로는 신고하는 userID가 추가됨
                        val blackList = BlackList(
                            user = userID ?: "ERROR",
                            blackedID = blackID,
                            blackedName = blackedName
                        )
                        scope.launch {
                            blackList(blackList)
                        }
                        navController.popBackStack()
                    }
                    .background(if (selectedOption.isNotEmpty()) Primary else Color.LightGray)
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text = "신고/차단 사유",
                        fontSize = 20.sp,
                    )
                }
                RadioButtonOption(
                    text = "도용",
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
                RadioButtonOption(
                    text = "상업적인 내용이 포함",
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
                RadioButtonOption(
                    text = "욕설/생명경시/혐오/차별적 표현",
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
                RadioButtonOption(
                    text = "불쾌한 표현",
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
                RadioButtonOption(
                    text = "기타",
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
                OutlinedTextField(
                    textStyle = LocalTextStyle.current,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(240.dp),
                    value = text,
                    onValueChange = { text = it },
                    maxLines = 500,
                    enabled = selectedOption == "기타",
                    placeholder = {
                        Text(text = "500자 이내로 신고/차단 내용을 써 주세요.")
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(text = "• 신고/차단시 해당 사용자의 작품/코멘트는 더 이상 보이지 않습니다.")
                    Text(text = "• 신고 내역은 관리자 내부 검토 후 내부정책에 의거하여 조치가 진행됩니다.")
                    Text(text = "• 설정-차단목록에서 차단 목록을 확인 하실 수 있습니다.")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReportBottomBar(
    isDarkTheme: Boolean,
    navController: NavHostController,
    modifier: Modifier,
) {
    Divider(color = if (isDarkTheme) Color.LightGray else Color.LightGray)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        )
        {
            Text(text = "취소")
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "확인", color = Color.White)
        }
    }
}

@Composable
private fun ReportTopBar(navController: NavHostController, isDarkTheme: Boolean) {
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
            TopBarTitleText("신고/차단", isDarkTheme)
            Spacer(modifier = Modifier.size(15.dp))
        }
        Divider(color = if (isDarkTheme) Color.LightGray else Color.LightGray)
    }
}

@Composable
fun RadioButtonOption(
    text: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = (text == selectedOption),
                onClick = { onOptionSelected(text) }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (text == selectedOption),
            onClick = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
        )
    }
}

data class BlackList(
    val user: String = "",
    val blackedID: String = "",
    val blackedName: String = ""
)

suspend fun blackList(blackList: BlackList) {
    val db = FirebaseFirestore.getInstance()
    val blackRef = db.collection("BlackList").document(blackList.user)
    val snapshot = blackRef.get().await()

    if (snapshot.exists()) {
        // 문서가 이미 존재하는 경우 업데이트합니다
        updateBlackList(blackList, blackRef)
    } else {
        // 문서가 존재하지 않는 경우 추가합니다
        addBlackList(blackList, blackRef)
    }
}

fun addBlackList(blackList: BlackList, blackRef: DocumentReference){
    val data = hashMapOf(blackList.blackedName to blackList.blackedID)
    blackRef.set(data)
}

suspend fun updateBlackList(blackList: BlackList, blackRef: DocumentReference){
    // 필드를 추가합니다
    val updates = hashMapOf<String, Any>(blackList.blackedName to blackList.blackedID)
    blackRef.update(updates).await()
}