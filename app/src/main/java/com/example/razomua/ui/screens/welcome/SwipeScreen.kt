

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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.razomua.model.SwipeAction
import com.example.razomua.ui.theme.Red
import com.example.razomua.ui.theme.White
import com.example.razomua.viewmodel.SwipeViewModel

@Composable
fun SwipeScreen(
    navController: NavController,
    viewModel: SwipeViewModel = viewModel()
) {
    val availableUsers by viewModel.availableUsers.collectAsState()
    val currentUserIndex by viewModel.currentUserIndex.collectAsState()
    val matchResult by viewModel.matchResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var offsetX by remember { mutableStateOf(0f) }
    val rotation by animateFloatAsState(targetValue = offsetX / 20)

    matchResult?.let { match ->
        MatchDialog(
            onDismiss = {
                viewModel.dismissMatchDialog()
                navController.navigate("chats")
            },
            onGoToChat = {
                viewModel.dismissMatchDialog()
                navController.navigate("chat/${match.chatId}")
            }
        )
    }

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
                    IconButton(onClick = { navController.navigate("chats") }) {
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
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                currentUserIndex >= availableUsers.size -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Користувачів більше немає ☹️",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAvailableUsers() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Red,
                                contentColor = White
                            )) {
                            Text("Оновити")
                        }
                    }
                }
                else -> {
                    val currentUser = availableUsers[currentUserIndex]

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(0.6f)
                                .offset(x = offsetX.dp)
                                .rotate(rotation)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragEnd = {
                                            when {
                                                offsetX > 200 -> {
                                                    viewModel.sendSwipe(currentUser.id, SwipeAction.LIKE)
                                                }
                                                offsetX < -200 -> {
                                                    viewModel.sendSwipe(currentUser.id, SwipeAction.DISLIKE)
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
                                    painter = painterResource(
                                        id = if (currentUser.imageRes != 0)
                                            currentUser.imageRes
                                        else
                                            R.drawable.pic_for_chat1
                                    ),
                                    contentDescription = "User photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(Color(0x99000000))
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "${currentUser.name}, 25",
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (currentUser.isOnline) {
                                        Text(
                                            text = "🟢 В мережі",
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
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
                                    viewModel.sendSwipe(currentUser.id, SwipeAction.DISLIKE)
                                    offsetX = 0f
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
                                    viewModel.sendSwipe(currentUser.id, SwipeAction.LIKE)
                                    offsetX = 0f
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
}

@Composable
fun MatchDialog(
    onDismiss: () -> Unit,
    onGoToChat: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Це Match!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF43A047)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ви сподобалися один одному!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onGoToChat,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF43A047)
                    )
                ) {
                    Text("Почати спілкування", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("Пізніше")
                }
            }
        }
    }
}