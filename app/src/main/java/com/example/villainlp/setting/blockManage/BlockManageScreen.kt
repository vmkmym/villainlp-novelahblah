package com.example.villainlp.setting.blockManage

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.shared.DefaultTopBar

@SuppressLint("SuspiciousIndentation")
@Composable
fun BlockManageScreen(
    navController: NavHostController,
    viewModel: BlockManageViewModel = viewModel(),
) {
    val isDarkTheme = isSystemInDarkTheme()

    viewModel.loadBlockList()

    val blackList by viewModel.blockedList.collectAsState()

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "차단 목록",
                navController = navController,
                isDarkTheme = isDarkTheme
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(blackList, key = { item -> item.uid }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = it.name)
                        OutlinedButton(onClick = { viewModel.unblock(it.uid) }) {
                            Text(
                                text = "차단 해제",
                                fontFamily = FontFamily(Font(R.font.yeongdeok_sea))
                            )
                        }
                    }
                }
                Divider()
            }
        }
    }
}
