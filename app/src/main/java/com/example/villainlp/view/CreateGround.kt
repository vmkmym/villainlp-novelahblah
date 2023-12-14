package com.example.villainlp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.villainlp.R

@Composable
fun CreativeYardScreen(navController: NavHostController) {

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

