package com.example.razomua.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.razomua.R
import com.example.razomua.ui.theme.Montserrat
import com.example.razomua.ui.theme.RazomUATheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Твої ідеальні зустрічі\nпочинаються тут",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3F51B5), // синій
            textAlign = TextAlign.Center,
            fontFamily = Montserrat,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = { navController.navigate("register") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Створити профіль", color = Color.White, fontSize = 16.sp, fontFamily = Montserrat )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Увійти", color = Color.White, fontSize = 16.sp, fontFamily = Montserrat)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    RazomUATheme {
        WelcomeScreen(navController = rememberNavController())
    }
}
