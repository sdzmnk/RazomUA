package com.example.razomua.ui.screens.welcome

import android.app.Dialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.razomua.R
import com.example.razomua.ui.theme.Montserrat

@Composable
fun ProfileScreen(navController: NavController) {
    var showQR by remember { mutableStateOf(false) }
    val testUserId = 12345
    val qrBitmap by remember {
        mutableStateOf(generateQRCode("https://razomua.com/profile/$testUserId"))
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
                .background(Color(0xFFF5F5F5))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Фото профілю
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(50.dp))
            ) {
                // Можна вставити Image(painter = ..., contentDescription = ...)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ім'я та вік
            Text(text = "Дар'я, 23", fontSize = 24.sp, fontFamily = Montserrat, color = Color.Black)

            // Місто
            Text(text = "Київ", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Що шукає
            Text(text = "Шукаю", fontFamily = Montserrat, color = Color.Blue)
            Surface(
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Дружнє спілкування",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontFamily = Montserrat
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Більше про мене
            Text(text = "Більше про мене", fontFamily = Montserrat, color = Color.Blue)

            Row(modifier = Modifier.padding(top = 4.dp)) {
                listOf("Плавання", "Вивчення мов").forEach { tag ->
                    Surface(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontFamily = Montserrat
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Кнопки
            Button(
                onClick = { navController.navigate("diagram") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Переглянути статистику", color = Color.White, fontFamily = Montserrat)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("googlemaps") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Хто поруч", color = Color.White, fontFamily = Montserrat)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showQR = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Згенерувати QR Code", color = Color.White, fontFamily = Montserrat)
            }

            if (showQR) {
                Dialog(onDismissRequest = { showQR = false }) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(250.dp)
                    )
                }
            }
        }
    }
}

