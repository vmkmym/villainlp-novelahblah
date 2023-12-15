package com.example.villainlp.view

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.villainlp.R
import com.example.villainlp.model.FirebaseTools.updateBookRating

@Composable
fun RatingScreen(navController: NavHostController, documentId: String) {
    var artistryRate by remember { mutableStateOf(0) }
    var originalityRate by remember { mutableStateOf(0) }
    var commercialViabilityRate by remember { mutableStateOf(0) }
    var literaryMeritRate by remember { mutableStateOf(0) }
    var completenessRate by remember { mutableStateOf(0) }
    var averageRate by remember { mutableStateOf(0.0f) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
                artistryRate = RatingBar(question = "작품성 (문장력과 구성력)")
                originalityRate = RatingBar(question = "독창성 (창조성과 기발함)")
                commercialViabilityRate = RatingBar(question = "상업성 (몰입도와 호소력)")
                literaryMeritRate = RatingBar(question = "문학성 (철학과 감명)")
                completenessRate = RatingBar(question = "완성도 (문체의 가벼움과 진지함)")
            }
        }

        averageRate = (artistryRate + originalityRate + commercialViabilityRate + literaryMeritRate + completenessRate) / 5.0f

        Row {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "취소")
            }
            Button(onClick = {
                updateBookRating(documentId, averageRate)
                navController.popBackStack()
            }) {
                Text(text = "제출")
            }
        }
    }
}

@Composable
fun RatingBar(question: String): Int {
    var rating by remember { mutableStateOf(0) }

    Text(text = question)
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display stars based on the selected rating
        (0 until 5).forEach { index ->
            val isSelected = index < rating
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
                        rating = index + 1
                    }
            )
        }
    }
    Divider()

    return rating
}