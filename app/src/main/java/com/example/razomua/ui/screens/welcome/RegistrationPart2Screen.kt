package com.example.razomua.ui.screens.welcome

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.razomua.viewmodel.RegisterViewModel

@Composable
fun RegistrationPart2Screen(navController: NavController, registerViewModel: RegisterViewModel) {
    val name = remember { mutableStateOf("") }
    val selectedGender = remember { mutableStateOf<String?>(null) }
    val day = remember { mutableStateOf("") }
    val month = remember { mutableStateOf("") }
    val year = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Давай знайомитися!",
            fontSize = 24.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Розкажи про себе — це допоможе створити профіль.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Як тебе звати?",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Під цим іменем тебе будуть бачити інші.",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name.value,
            onValueChange = { name.value = it },
            placeholder = { Text("Ім'я") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ваш гендер?",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedGender.value == "Жінка",
                onClick = { selectedGender.value = "Жінка" },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE74C3C))
            )
            Text("Жінка", fontSize = 16.sp, modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = selectedGender.value == "Чоловік",
                onClick = { selectedGender.value = "Чоловік" },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE74C3C))
            )
            Text("Чоловік", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Коли у тебе день народження?",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = day.value,
                onValueChange = { day.value = it },
                placeholder = { Text("День") },
                modifier = Modifier
                    .width(90.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = month.value,
                onValueChange = { month.value = it },
                placeholder = { Text("Місяць") },
                modifier = Modifier
                    .width(90.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = year.value,
                onValueChange = { year.value = it },
                placeholder = { Text("Рік") },
                modifier = Modifier
                    .width(90.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Додай свою фотографію",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable { /* TODO: handle photo upload */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Додати фото",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = {
                    val birthday = "${day.value}.${month.value}.${year.value}"
                    registerViewModel.completeRegistration(name.value, selectedGender.value, birthday)
                    registerViewModel.logAllUsers()
                    navController.navigate("register3")},
                containerColor = Color(0xFFE74C3C)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Далі", tint = Color.White)
            }
        }
    }
}


