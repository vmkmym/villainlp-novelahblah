@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import com.example.villainlp.model.CreativeYard
import com.example.villainlp.model.Screen

@Composable
fun CreativeYardScreen(navController: NavHostController) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        item {
            CreativeCard(
                cardColor = Color(0xFFEAFFF2),
                cardTitle = "작가의 마당",
                cardDescription = "스토리라인이 있는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_1)
            ) {
                // 클릭 시 이벤트 처리
                // 새로운 채팅방 열기 등
            }
        }
        item {
            CreativeCard(
                cardColor = Color(0xFFFFFEE3),
                cardTitle = "꿈의 마당",
                cardDescription = "스토리라인이 없는 경우",
                imageResource = painterResource(id = R.drawable.creative_yard_2)
            ) {
                // 클릭 시 이벤트 처리
                // 새로운 채팅방 열기 등
            }
        }
        // 추가적인 카드들을 여기에 추가
    }
}

@Composable
fun CreativeCard(
    cardColor: Color,
    cardTitle: String,
    cardDescription: String,
    imageResource: Painter, // 이미지를 사용하기 위한 Painter
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .shadow(elevation = 8.dp, spotColor = cardColor, ambientColor = cardColor)
            .width(180.dp)
            .height(226.dp)
            .background(color = cardColor, shape = RoundedCornerShape(size = 17.dp)),
        onClick = onClick
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
                        x = 28.dp,
                        y = 28.dp
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
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
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}



@Preview
@Composable
fun CreativeYardScreenPreview() {
    val navController = rememberNavController()
    CreativeYardScreen(navController = navController)
}



//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyScaffold() {
//    Scaffold(
//        topBar = {
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp)
//                        .background(color = Color(0xFFF4F4F4))
//                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
//                    horizontalArrangement = Arrangement.Center,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    Text(
//                        text = "Home",
//                        style = TextStyle(
//                            fontSize = 22.sp,
//                            fontWeight = FontWeight(500),
//                            color = Color(0xFF212121),
//                        )
//                    )
//                }
//                Divider(color = Color(0xFF9E9E9E))
//            }
//        },
//        bottomBar = {
//            Row(
//                Modifier
//                    .shadow(
//                        elevation = 16.dp,
//                        spotColor = Color(0x3817C3CE),
//                        ambientColor = Color(0x3817C3CE)
//                    )
//                    .width(428.dp)
//                    .height(100.dp)
//                    .background(color = Color(0xFFF4F4F4))
//                    .padding(start = 66.dp, top = 21.dp, end = 66.dp, bottom = 21.dp),
//                horizontalArrangement = Arrangement.spacedBy(96.dp, Alignment.CenterHorizontally),
//                verticalAlignment = Alignment.Top,
//            ) {
//                // Child views.
//                IconButton(onClick = { /*TODO*/ }) {
//                    Column(verticalArrangement = Arrangement.Center) {
//                        Image(
//                            modifier = Modifier
//                                .padding(0.dp)
//                                .width(28.dp)
//                                .height(28.dp),
//                            painter = painterResource(id = R.drawable.message),
//                            contentDescription = "image description",
//                        )
//                        Text(
//                            modifier = Modifier
//                                .width(23.dp)
//                                .height(17.dp),
//                            text = "Chat",
//                            style = TextStyle(
//                                fontSize = 10.sp,
//                                fontWeight = FontWeight(500),
//                                color = Color(0xFFBBBBBB),
//                            )
//                        )
//                    }
//                }
//                IconButton(onClick = { /*TODO*/ }) {
//                    Column(verticalArrangement = Arrangement.Center) {
//                        Image(
//                            modifier = Modifier
//                                .padding(0.dp)
//                                .width(26.dp)
//                                .height(26.dp),
//                            painter = painterResource(id = R.drawable.archive_add),
//                            contentDescription = "image description",
//                        )
//                        Text(
//                            modifier = Modifier
//                                .width(30.dp)
//                                .height(17.dp),
//                            text = "Saved",
//                            style = TextStyle(
//                                fontSize = 10.sp,
//                                fontWeight = FontWeight(500),
//                                color = Color(0xFFBBBBBB),
//                            )
//                        )
//                    }
//                }
//
//            }
//        }
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(it), contentAlignment = Alignment.Center
//        ) {
//            Text("Body Content")
//        }
//    }
//}

