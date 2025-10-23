package com.example.razomua.ui.screens.welcome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.razomua.model.SwipeAction
import com.example.razomua.viewmodel.SwipeViewModel

@Composable
fun SwipeScreen(navController: NavController, viewModel: SwipeViewModel = viewModel()) {
    var offsetX by remember { mutableStateOf(0f) }
    var currentCardIndex by remember { mutableStateOf(0) }

    val users = listOf(
        Triple("Юрій", "Київ", R.drawable.boy),
        Triple("Анна", "Львів", R.drawable.girl),
        Triple("Олег", "Харків", R.drawable.boy)
    )

    val rotation by animateFloatAsState(targetValue = offsetX / 20)

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("chat") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.chats),
                            contentDescription = "Chats",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(onClick = { navController.navigate("swipe") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.cards),
                            contentDescription = "Swipes",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (currentCardIndex >= users.size) {
                Text(
                    "Користувачів більше немає ☹️",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                val (name, location, imageRes) = users[currentCardIndex]

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.6f)
                            .offset(x = offsetX.dp)
                            .rotate(rotation)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        when {
                                            offsetX > 200 -> {
                                                viewModel.addSwipe(1, currentCardIndex.toLong(), SwipeAction.LIKE)
                                                currentCardIndex++
                                            }
                                            offsetX < -200 -> {
                                                viewModel.addSwipe(1, currentCardIndex.toLong(), SwipeAction.DISLIKE)
                                                currentCardIndex++
                                            }
                                        }
                                        offsetX = 0f
                                    }
                                ) { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "User photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(modifier = Modifier.align(Alignment.TopStart).padding(12.dp)) {
                                Text(
                                    text = "$name, 25",
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 12.dp, top = 40.dp)
                                    .background(
                                        color = Color(0x66000000),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("📍 $location", color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.addSwipe(1, currentCardIndex.toLong(), SwipeAction.DISLIKE)
                                currentCardIndex++
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDECEC))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dislike",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.addSwipe(1, currentCardIndex.toLong(), SwipeAction.LIKE)
                                currentCardIndex++
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Like",
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
