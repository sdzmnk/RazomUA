package com.example.razomua.ui.screens.welcome

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.razomua.R
import com.example.razomua.model.ChatUser
import com.example.razomua.viewmodel.ChatViewModel
import java.util.Date
import java.util.Locale

@Composable
fun ListChatsScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val users by viewModel.users.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val chats = users

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Чати",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                if (isConnected) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                }
            }

            if (chats.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Поки що немає чатів",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(chats) { chat ->
                        ChatRow(
                            chat = chat,
                            navController = navController,
                            onChatClick = { selectedChat ->
                                val currentUserId = viewModel.getCurrentUserId()
                                if (currentUserId != null) {
                                    val sortedIds = listOf(currentUserId, selectedChat.id).sorted()
                                    val chatId = "${sortedIds[0]}_${sortedIds[1]}"

                                    Log.d("ListChatsScreen", "Current user: $currentUserId")
                                    Log.d("ListChatsScreen", "Other user: ${selectedChat.id}")
                                    Log.d("ListChatsScreen", "Generated chatId: $chatId")

                                    viewModel.connectToChat(chatId)
                                    navController.navigate("chat/$chatId")
                                } else {
                                    Log.e("ListChatsScreen", "Current user ID is null!")
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRow(
    chat: ChatUser,
    navController: NavController,
    onChatClick: (ChatUser) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onChatClick(chat) }
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (chat.photoUrl.isNotEmpty()) chat.photoUrl else R.drawable.pic_for_chat1,
            contentDescription = chat.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            error = painterResource(R.drawable.pic_for_chat1),
            placeholder = painterResource(R.drawable.pic_for_chat1)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A0000)
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (chat.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                } else {
                    Text(
                        text = "• ${getLastSeenText(chat.lastSeen)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chat.lastMessage,
                fontSize = 14.sp,
                color = Color(0xFF4A4A4A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getLastSeenText(lastSeen: Long): String {
    if (lastSeen == 0L) return "давно не заходив"

    val now = System.currentTimeMillis()
    val diff = now - lastSeen

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "щойно"
        minutes < 60 -> "$minutes хв. тому"
        hours < 24 -> "$hours год. тому"
        days == 1L -> "вчора"
        days < 7 -> "$days дн. тому"
        else -> {
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(Date(lastSeen))
            date
        }
    }
}