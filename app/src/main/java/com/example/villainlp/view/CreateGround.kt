@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.villainlp.R

@Composable
fun CreativeYardScreen(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(color = Color(0xFFF4F4F4))
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Home",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFF212121),
                        )
                    )
                }
                Divider(color = Color(0xFF9E9E9E))
            }
        },
        bottomBar = {
            Row(
                Modifier
                    .shadow(
                        elevation = 16.dp,
                        spotColor = Color(0x3817C3CE),
                        ambientColor = Color(0x3817C3CE)
                    )
                    .width(428.dp)
                    .height(80.dp)
                    .background(color = Color(0xFFF4F4F4))
                    .padding(start = 66.dp, top = 21.dp, end = 66.dp, bottom = 21.dp),
                horizontalArrangement = Arrangement.spacedBy(96.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Top,
            ) {
                // Child views.
                IconButton(onClick = { /*TODO*/ }) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Image(
                            modifier = Modifier
                                .padding(0.dp)
                                .width(28.dp)
                                .height(28.dp),
                            painter = painterResource(id = R.drawable.message),
                            contentDescription = "image description",
                        )
                        Text(
                            modifier = Modifier
                                .width(23.dp)
                                .height(17.dp),
                            text = "Chat",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFFBBBBBB),
                            )
                        )
                    }
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Image(
                            modifier = Modifier
                                .padding(0.dp)
                                .width(26.dp)
                                .height(26.dp),
                            painter = painterResource(id = R.drawable.archive_add),
                            contentDescription = "image description",
                        )
                        Text(
                            modifier = Modifier
                                .width(30.dp)
                                .height(17.dp),
                            text = "Saved",
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight(500),
                                color = Color(0xFFBBBBBB),
                            )
                        )
                    }
                }

            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .padding(it)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start,
            ) {
                LazyRow (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryDefalutButton()
                    }
                    items(genreHappy.size) { index ->
                        CategoryButton(genreHappy[index])
                    }
                }
                LazyRow (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genreSad.size) { index ->
                        CategoryButton(genreSad[index])
                    }
                }
                LazyRow (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genreScary.size) { index ->
                        CategoryButton(genreScary[index])
                    }
                }
                LazyColumn {
                    item {
                        Header()
                        CreativeYard()
                        CreativeYard()
                    }
                }
            }
        }
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .padding(start = 12.dp, top = 20.dp, end = 16.dp, bottom = 12.dp)
    ) {
        Text(
            text = "✏️ 당신만의 소설을 써보세요",
            style = TextStyle(
                fontSize = 22.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(600),
                color = Color(0xFF000000)
            )
        )
    }
}

@Composable
fun CreativeYard() {
    LazyRow {
        item {
            CreativeCard(
                cardColor = Color(0xFFC3FCD9),
                cardTitle = "작가의 마당",
                cardDescription = "스토리라인이 있는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_1),
                onCardClick = { /*TODO*/ },
            ) {
                // 클릭 시 이벤트 처리
                // 새로운 채팅방 열기 등
            }
        }
        item {
            CreativeCard(
                cardColor = Color(0xFFFFEAEA),
                cardTitle = "꿈의 마당",
                cardDescription = "스토리라인이 없는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_2),
                onCardClick = { /*TODO*/ },
            ) {
                // 클릭 시 이벤트 처리
                // 새로운 채팅방 열기 등
            }
        }
        item {
            CreativeCard(
                cardColor = Color(0xFFFFE8C6),
                cardTitle = "아이디어 마당",
                cardDescription = "아이디어를 나누어봐요",
                imageResource = painterResource(id = R.drawable.idea),
                onCardClick = { /*TODO*/ },
            ) {
                // 클릭 시 이벤트 처리
                // 새로운 채팅방 열기 등
            }
        }
    }
}

@Composable
fun CreativeCard(
    cardColor: Color,
    cardTitle: String,
    cardDescription: String,
    imageResource: Painter,
    onCardClick: () -> Unit,
    onDialogConfirm: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .clickable { showDialog = true }
            .padding(8.dp)
            .shadow(elevation = 8.dp, spotColor = cardColor, ambientColor = cardColor)
            .width(180.dp)
            .height(226.dp)
            .background(color = cardColor, shape = RoundedCornerShape(size = 17.dp)),
        onClick = onCardClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = imageResource,
                contentDescription = "Card Image",
                modifier = Modifier
                    .size(66.dp)
                    .offset(
                        x = 16.dp,
                        y = 20.dp
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .offset(
                        x = 16.dp,
                        y = 100.dp
                    )
            ) {
                Text(
                    text = cardTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cardDescription,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = "작가의 마당 채팅방을 생성하시겠습니까?")
                },
                text = {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("채팅방 이름을 입력해주세요") }
                    )
                },
                confirmButton = {
                    IconButton(
                        onClick = {
                            onDialogConfirm(title)
                            showDialog = false
                        }
                    ) {
                        Text(text = "확인")
                    }
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
}


@Composable
fun CategoryButton(genre: String) {
    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = Color(0xFF17C3CE),
                shape = RoundedCornerShape(size = 17.dp)
            )
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Text(
            text = genre,
            style = TextStyle (
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFF17C3CE)
            )
        )
    }
}

@Composable
fun CategoryDefalutButton() {
    Box(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .height(34.dp)
            .background(color = Color(0xFF17C3CE), shape = RoundedCornerShape(size = 17.dp))
            .padding(start = 22.dp, top = 7.dp, end = 22.dp, bottom = 7.dp)
    ) {
        Text(
            text = "All",
            style = TextStyle (
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight(500),
                color = Color(0xFFFFFFFF)
            )
        )
    }
}


val genreHappy = listOf(
    "Fantasy", "Romance", "Comedy"
)

val genreScary = listOf(
    "Horror", "Thriller", "Mystery", "SF"
)

val genreSad = listOf(
    "Melodrama", "Tragedy", "Family", "others"
)

