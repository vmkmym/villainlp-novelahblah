package com.example.villainlp.novel.library.rating

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.ui.theme.Blue789
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RatingScreen(
    navController: NavHostController,
    documentId: String,
    viewModel: RatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    // rating요소 5개의 상태를 나타냄
    val ratingViewState by viewModel.ratingViewState.collectAsState()

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                RatingBar(
                    question = "작품성 (문장력과 구성력)",
                    rating = ratingViewState.artistryRate,
                    onRatingChanged = { viewModel.setArtistryRate(it) }
                )
                RatingBar(
                    question = "독창성 (창조성과 기발함)",
                    rating = ratingViewState.originalityRate,
                    onRatingChanged = { viewModel.setOriginalityRate(it) }
                )
                RatingBar(
                    question = "상업성 (몰입도와 호소력)",
                    rating = ratingViewState.commercialViabilityRate,
                    onRatingChanged = { viewModel.setCommercialViabilityRate(it) }
                )
                RatingBar(
                    question = "문학성 (철학과 감명)",
                    rating = ratingViewState.literaryMeritRate,
                    onRatingChanged = { viewModel.setLiteraryMeritRate(it) }
                )
                RatingBar(
                    question = "완성도 (문체의 가벼움과 진지함)",
                    rating = ratingViewState.completenessRate,
                    onRatingChanged = { viewModel.setCompletenessRate(it) }
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center, // 버튼들을 수평 가운데로 정렬
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp) // 상하 여백 추가
        ) {
            Button(
                onClick = {
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            )
            {
                Text(
                    text = "취소",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
            Button(
                onClick = {
                    scope.launch { viewModel.submitRating(documentId) }
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(
                    text = "제출",
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue789
                    )
                )
            }
        }
    }
}

@Composable
fun RatingBar(
    question: String,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
) {
    //전달 받은 rating을 지역변수로 사용함
    var localRating by remember { mutableStateOf(rating) }

    Text(text = question)
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display stars based on the selected rating
        (0 until 5).forEach { index ->
            val isSelected = index < localRating
            val starIcon: Painter = if (isSelected) {
                painterResource(id = R.drawable.ic_star_filled)
            } else {
                painterResource(id = R.drawable.ic_star_outline)
            }

            Image(
                painter = starIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        localRating = index + 1
                        onRatingChanged(localRating) // Int값을 반환하기 우해
                    }
            )
        }
    }
    Divider()
}