package com.example.razomua.ui.screens.register

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.razomua.ui.theme.Blue
import com.example.razomua.ui.theme.GrayMedium

@Preview(showBackground = true)
@Composable
fun RegisterScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Введи свої дані, щоб продовжити",
                fontSize = 24.sp,
                color = Blue,
                modifier = Modifier.padding(top = 80.dp, bottom = 40.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("example@gmail.com", color = GrayMedium) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GrayMedium, CircleShape),
                shape = CircleShape,
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Пароль", color = GrayMedium) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GrayMedium, CircleShape),
                shape = CircleShape
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = { /* TODO: логіка входу */ },
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .size(70.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Login",
                    tint = Color.White
                )
            }
        }
    }
}
