package com.example.villainlp.novel.library

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.villainlp.R
import com.example.villainlp.shared.MyLibraryScaffold
import com.example.villainlp.shared.ShowAllBooks
import com.example.villainlp.ui.theme.Blue789
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LibraryScreen(
    navController: NavHostController,
    auth: FirebaseAuth,
    viewModel: LibraryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val novelList by viewModel.novelList.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val isRateClicked by viewModel.isRateClicked.collectAsState()
    val isViewClicked by viewModel.isViewClicked.collectAsState()
    val isUpdateClicked by viewModel.isUpdateClicked.collectAsState()

    val starIcon = if(isRateClicked) R.drawable.star_white else R.drawable.star_sky
    val viewIcon = if(isViewClicked) R.drawable.views_white else R.drawable.views
    val updateIcon = if(isUpdateClicked) R.drawable.clock_white else R.drawable.clock

    // 정렬이 바뀔때마다 Novels를 로드하는 방식이 달라짐
    viewModel.loadNovels()

    MyLibraryScaffold(
        "도서관", navController
    ) {
        Column(
            modifier = it.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SortOptionButton(
                    image = starIcon,
                    backColor = if (isRateClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.rateClicked() }
                )
                SortOptionButton(
                    image = viewIcon,
                    backColor = if (isViewClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.viewClicked() }
                )
                SortOptionButton(
                    image = updateIcon,
                    backColor = if (isUpdateClicked) Color(0xFF17C3CE) else Color.White,
                    onClicked = { viewModel.updateClicked() }
                )
            }
            ShowAllBooks(navController, novelList, auth) { selectedNovel -> viewModel.onDeleteClicked(selectedNovel) }
        }
    }

    if (showDialog) {
        DeleteAlert(
            title = "정말로 삭제하시겠습니까?",
            warningMessage = "선택한 소설이 삭제됩니다.",
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = { viewModel.onConfirmClicked() }
        )
    }
}

@Composable
fun DeleteAlert(
    title: String,
    warningMessage: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
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
            Text(
                text = title,
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
                Text(
                    text = warningMessage,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            IconButton(
                onClick = { onConfirm() }
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
                onClick = { onDismiss() }
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
        modifier = Modifier.padding(16.dp)
    )
}


@Composable
fun SortOptionButton(
    image: Int,
    backColor: Color,
    onClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = Color(0xFF17C3CE),
                shape = RoundedCornerShape(size = 17.dp)
            )
            .background(
                color = backColor,
                shape = RoundedCornerShape(size = 17.dp)
            )
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .clickable { onClicked() }
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Sort Options",
        )
    }
}
