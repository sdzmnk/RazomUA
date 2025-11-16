package com.example.razomua.ui.screens.welcome

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.websocketchatapp.WebSocketViewModel

data class ChatUser(
    val name: String,
    val message: String,
    val image: Int,
    val isOnline: Boolean
)

@Composable
fun ListChatsScreen(
    navController: NavController,
    viewModel: WebSocketViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.connect()
    }

    val isConnected by viewModel.isConnected.collectAsState()

    val chats by remember(isConnected) {
        mutableStateOf(
            listOf(
                ChatUser("Андрій", "Привіт, як справи?", com.example.razomua.R.drawable.pic_for_chat1, isOnline = isConnected),
                ChatUser("Славік", "Буде зустріч завтра?", com.example.razomua.R.drawable.pic_for_chat2, isOnline = isConnected),
            )
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
            }

            LazyColumn {
                items(chats) { chat ->
                    ChatRow(chat,  navController = navController)
                    Divider(color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun ChatRow(chat: ChatUser, navController: NavController) {
    Row(
        modifier = Modifier
                .clickable {
            navController.navigate("chat")}
                .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = chat.image),
            contentDescription = chat.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2A0000)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (chat.isOnline) Color.Green else Color.Gray)
                )
            }
            Text(
                text = chat.message,
                fontSize = 14.sp,
                color = Color(0xFF4A4A4A)
            )
        }
    }
}
