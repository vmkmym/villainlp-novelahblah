package com.example.villainlp.novel.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.villainlp.novel.myNovel.readMyNovel.OnlyTopBarScaffold
import com.example.villainlp.ui.theme.VillainlpTheme

@Composable
fun ReportScreen(navController: NavHostController){
    OnlyTopBarScaffold(
        title = "신고/차단하기",
        navController = navController,
        hasIcon = false,
        onClicked = {  },
        content = {

        },
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VillainlpTheme {
        val navController = rememberNavController()
        RadioButtonExample(navController = navController)
    }
}

@Composable
fun RadioButtonExample(navController: NavHostController) {
    var selectedOption by remember { mutableStateOf("") }
    var text by remember {
        mutableStateOf("")
    }

    OnlyTopBarScaffold(
        title = "신고/차단하기",
        navController = navController,
        hasIcon = false,
        content = {
            Column(
                modifier = it
            ) {
                Text(text = "신고/차단 사유")
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
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(240.dp),
                    value = text,
                    onValueChange = { text = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Text(text = "• 신고/차단시 해당 사용자의 작품/코멘트는 더 이상 보이지 않습니다.")
                Text(text = "• 신고 내역은 관리자 내부 검토 후 내부정책에 의거하여 조치가 진행됩니다.")
                Text(text = "• 설정-차단목록에서 차단 목록을 확인 하실 수 있습니다.")
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                        Text(text = "취소")
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                        Text(text = "확인")
                    }
//                    Button(
//                        onClick = {
//                            // 확인 버튼을 클릭했을 때 선택한 옵션을 표시
//                            if (selectedOption.isNotEmpty()) {
//                                // 여기에서 선택한 옵션을 사용할 수 있습니다.
//                                // 여기에서는 간단히 로그로 출력합니다.
//                                println("선택한 옵션: $selectedOption")
//                            }
//                        },
//                    ) {
//                        Text(text = "취소")
//                    }
//                    Button(
//                        onClick = {
//                            // 확인 버튼을 클릭했을 때 선택한 옵션을 표시
//                            if (selectedOption.isNotEmpty()) {
//                                // 여기에서 선택한 옵션을 사용할 수 있습니다.
//                                // 여기에서는 간단히 로그로 출력합니다.
//                                println("선택한 옵션: $selectedOption")
//                            }
//                        },
//                    ) {
//                        Text(text = "확인")
//                    }
                }
            }
        },
        onClicked = {},
    )
}

@Composable
fun RadioButtonOption(
    text: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
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