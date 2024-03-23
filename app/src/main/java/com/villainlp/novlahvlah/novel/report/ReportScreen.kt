package com.villainlp.novlahvlah.novel.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.villainlp.novlahvlah.shared.DefaultTopBar
import com.villainlp.novlahvlah.ui.theme.Primary

@Composable
fun ReportScreen(
    navController: NavHostController,
    blackID: String,
    blackedName: String,
    viewModel: ReportViewModel = viewModel(),
) {
    val selectedOption by viewModel.selectedOption.collectAsState()
    val text by viewModel.text.collectAsState()

    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "신고/차단",
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
                        viewModel.submitReport(blackID, blackedName)
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
                    onOptionSelected = { viewModel.onSeleceted(it) }
                )
                RadioButtonOption(
                    text = "상업적인 내용이 포함",
                    selectedOption = selectedOption,
                    onOptionSelected = { viewModel.onSeleceted(it) }
                )
                RadioButtonOption(
                    text = "욕설/생명경시/혐오/차별적 표현",
                    selectedOption = selectedOption,
                    onOptionSelected = { viewModel.onSeleceted(it) }
                )
                RadioButtonOption(
                    text = "불쾌한 표현",
                    selectedOption = selectedOption,
                    onOptionSelected = { viewModel.onSeleceted(it) }
                )
                RadioButtonOption(
                    text = "기타",
                    selectedOption = selectedOption,
                    onOptionSelected = { viewModel.onSeleceted(it) }
                )
                OutlinedTextField(
                    textStyle = LocalTextStyle.current,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(240.dp),
                    value = text,
                    onValueChange = { viewModel.onTextChange(it) },
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

