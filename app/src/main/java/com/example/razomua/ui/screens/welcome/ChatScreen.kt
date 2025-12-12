//package com.example.razomua.ui.screens.welcome
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.example.razomua.R
//import com.example.websocketchatapp.WebSocketViewModel
//import com.example.razomua.ui.screens.welcome.components.MessageBubble
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    viewModel: WebSocketViewModel,
//    navController: NavController,
//    modifier: Modifier = Modifier
//) {
//    val messages by remember { derivedStateOf { viewModel.messages } }
//    val isConnected by viewModel.isConnected.collectAsState()
//    val listState = rememberLazyListState()
//
//    LaunchedEffect(Unit) {
//        viewModel.connect()
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            viewModel.disconnect()
//        }
//    }
//
//    LaunchedEffect(messages) {
//        if (messages.isNotEmpty()) {
//            listState.animateScrollToItem(messages.size - 1)
//        }
//    }
//
//    Scaffold(
//        bottomBar = {
//            BottomAppBar(
//                containerColor = Color.White,
//                tonalElevation = 4.dp
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.SpaceEvenly,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    IconButton(onClick = { navController.navigate("chats") }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.chats),
//                            contentDescription = "Chats",
//                            tint = Color.Black,
//                            modifier = Modifier.size(28.dp)
//                        )
//                    }
//
//                    IconButton(onClick = { navController.navigate("swipe") }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.cards),
//                            contentDescription = "Swipes",
//                            tint = Color.Black,
//                            modifier = Modifier.size(28.dp)
//                        )
//                    }
//
//                    IconButton(onClick = { navController.navigate("profile") }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.user),
//                            contentDescription = "Profile",
//                            tint = Color.Black,
//                            modifier = Modifier.size(28.dp)
//                        )
//                    }
//                }
//            }
//        }
//    ) { innerPadding ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth(),
//                state = listState,
//                contentPadding = PaddingValues(vertical = 8.dp)
//            ) {
//                items(messages) { message ->
//                    MessageBubble(message)
//                }
//            }
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                var messageText by remember { mutableStateOf("") }
//
//                OutlinedTextField(
//                    value = messageText,
//                    onValueChange = { messageText = it },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 8.dp),
//                    placeholder = { Text("Type a message") },
//                    shape = RoundedCornerShape(24.dp)
//                )
//
//                Button(
//                    onClick = {
//                        if (messageText.isNotBlank()) {
//                            viewModel.sendMessage(messageText)
//                            messageText = ""
//                        }
//                    },
//                    enabled = isConnected && messageText.isNotBlank()
//                ) {
//                    Text("Send")
//                }
//            }
//        }
//    }
//}
package com.example.razomua.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.razomua.viewmodel.ChatViewModel
import com.example.razomua.ui.screens.welcome.components.MessageBubble
import com.example.razomua.ui.theme.GrayLight
import com.example.razomua.ui.theme.GrayMedium
import com.example.razomua.ui.theme.Red
import com.example.razomua.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    viewModel: ChatViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.connectToChat(chatId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Чат", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Red,
                    titleContentColor = White
                )
            )
        },
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
                .padding(innerPadding)
                .background(White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
            }

            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(IntrinsicSize.Min), // ряд підлаштовується під найвищий елемент
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight() 
                            .padding(end = 8.dp),
                        placeholder = { Text("Введіть повідомлення...") },
                        shape = RoundedCornerShape(24.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        },
                        enabled = messageText.isNotBlank() && isConnected,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red,
                            contentColor = White,
                            disabledContainerColor = GrayLight,
                            disabledContentColor = White
                        ),
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Text("Надіслати")
                    }
                }

            }
        }
    }
}