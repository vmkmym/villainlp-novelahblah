package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.villainlp.R

@Composable
fun RatingScreen(navController: NavHostController) {
    var artistryRate by remember { mutableStateOf(0) }
    var originalityRate by remember { mutableStateOf(0) }
    var commercialViabilityRate by remember { mutableStateOf(0) }
    var literaryMeritRate by remember { mutableStateOf(0) }
    var completenessRate by remember { mutableStateOf(0) }
    var isTest by remember { mutableStateOf(false) }

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
        Button(onClick = { isTest = true }, enabled = !isTest) {
            Text(text = "완료")
        }
        if (isTest) {
            Text(text = "$artistryRate")
            Text(text = "$originalityRate")
            Text(text = "$commercialViabilityRate")
            Text(text = "$literaryMeritRate")
            Text(text = "$completenessRate")
        }
    }
}

@Composable
fun Test2() {

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RatingBar(question: String): Int {
    var rating by remember { mutableStateOf(0) }
    var isRatingSubmitted by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        if (!isRatingSubmitted) {
                            rating = index + 1
                        }
                    }
            )
        }

        // Submit button
        Button(
            onClick = {
                // Handle the rating submission logic here
                isRatingSubmitted = true
                focusManager.clearFocus()
                keyboardController?.hide()
            },
            enabled = !isRatingSubmitted
        ) {
            Text(text = "Submit Rating")
        }
    }
    Divider()

    // Display the selected rating
//    if (isRatingSubmitted) {
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = "$question : $rating")
//    }

    return rating
}