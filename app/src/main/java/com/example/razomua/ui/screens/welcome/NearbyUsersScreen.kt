package com.example.razomua.ui.screens.welcome

import android.bluetooth.BluetoothAdapter
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.razomua.R
import com.example.razomua.ui.bluetooth.BluetoothScanner
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.razomua.ui.theme.Blue
import com.example.razomua.ui.theme.Montserrat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Модель користувача
data class NearbyUser(
    val id: String,
    val name: String,
    val distance: Double
)

// Компонент рядка користувача
@Composable
fun NearbyUserItem(user: NearbyUser, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Відстань: ${user.distance} м")
            }
        }
    }
}

@Composable
fun NearbyUsersScreen(navController: NavController, onUserClick: (String) -> Unit) {
    val context = LocalContext.current
    val scanner = remember { BluetoothScanner(context) }
    val scope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }

    val testUsers = listOf(
        NearbyUser("1", "Anna", 1.5),
        NearbyUser("2", "Maksym", 3.4),
        NearbyUser("3", "Oleg", 6.2),
        NearbyUser("4", "Marina", 2.7),
        NearbyUser("5", "Sasha", 8.0)
    )

    // Простий State для показу тестових юзерів після 4 секунд
    var showTestUsers by remember { mutableStateOf(false) }

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
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Spacer(Modifier.height(25.dp))
            Text("Користувачі поруч", color = Blue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp)
            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    isScanning = true
                    scanner.startScan()

                    scope.launch {
                        delay(4000)
                        scanner.stopScan()
                        isScanning = false
                        showTestUsers = true // показуємо тестових юзерів після 4 секунд
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                enabled = !isScanning,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isScanning) "Сканування..." else "Сканувати", fontFamily = Montserrat)
            }

            Spacer(Modifier.height(16.dp))

            if (isScanning) {
                CircularProgressIndicator()
            }

            Spacer(Modifier.height(16.dp))

            if (showTestUsers) {
                LazyColumn {
                    items(testUsers) { user ->
                        NearbyUserItem(user = user, onClick = { onUserClick(user.id) })
                    }
                }
            }
        }
    }
}
