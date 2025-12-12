package com.example.razomua.ui.screens.welcome

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.razomua.repository.FirebaseChatRepository
import com.example.razomua.repository.FirebaseSwipeRepository
import com.example.razomua.ui.theme.Red
import com.example.razomua.ui.theme.White
import com.example.razomua.viewmodel.SwipeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.razomua.viewmodel.SwipeViewModelFactory


@Composable
fun DiagramScreen(navController: NavController,  swipeRepository: FirebaseSwipeRepository,
                  chatRepository: FirebaseChatRepository
) {
    val viewModel: SwipeViewModel = viewModel(factory = SwipeViewModelFactory(swipeRepository))

    val likes by viewModel.likesReceived.collectAsState(initial = 0)


    var matchesCount by remember { mutableStateOf(0) }
    var messagesCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            swipeRepository.getUserMatchesCountFlow().collect { count ->
                matchesCount = count
            }
        } catch (e: Exception) {
            Log.e("DiagramScreen", "Error collecting matchesCount", e)
        }
    }

    LaunchedEffect(Unit) {
        try {
            chatRepository.getUserMessagesCountFlow().collect { count ->
                messagesCount = count
            }
        } catch (e: Exception) {
            Log.e("DiagramScreen", "Error collecting messagesCount", e)
        }
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
                    IconButton(
                        onClick = { navController.navigate("chats") },
                        modifier = Modifier.testTag("ChatsButton")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.chats),
                            contentDescription = "Chats",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = { navController.navigate("swipe") },
                        modifier = Modifier.testTag("SwipesButton")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cards),
                            contentDescription = "Swipes",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.testTag("ProfileButton")
                    ) {
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
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Text(
                    text = "Статистика",
                    fontSize = 28.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Показники активності користувача",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(20.dp))

                StatisticCard(
                    iconId = R.drawable.cards,
                    title = "Кількість мeчів",
                    value = matchesCount,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    iconId = R.drawable.chats,
                    title = "Кількість повідомлень",
                    value = messagesCount,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatisticCard(
                    iconId = R.drawable.heart,
                    title = "Отримано лайків",
                    value = likes,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
@Composable
fun StatisticCard(
    iconId: Int,
    title: String,
    value: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Red,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = White,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            color = White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value.toString(),
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

