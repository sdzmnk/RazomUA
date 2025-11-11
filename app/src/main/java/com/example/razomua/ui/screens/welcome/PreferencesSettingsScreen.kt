package com.example.razomua.ui.screens.welcome

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.razomua.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferencesSettingsScreen(navController: NavController) {
    val searchQuery = remember { mutableStateOf("") }
    val selectedPurpose = remember { mutableStateOf<String?>(null) }
    val selectedGender = remember { mutableStateOf<String?>(null) }
    val selectedHobbies = remember { mutableStateListOf<String>() }

    val hobbyCategories = mapOf(
        "Активність" to listOf("Йога", "Тренажерний зал", "Біг", "Танці", "Плавання"),
        "Творчість" to listOf("Малювання", "Дизайн", "Гра на музичних інструментах", "Фотографія"),
        "Розваги" to listOf("Подорожі", "Час з сім’єю", "Кіно", "Волонтерство"),
        "Інтелект" to listOf("Читання", "Настільні ігри", "Вивчення мов", "Шахи")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Майже у цілі!",
            fontSize = 24.sp,
            color = Blue,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Відповідай на питання, щоб заповнити свій профіль.",
            fontSize = 14.sp,
            color = GrayDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Звідки ти?",
            fontSize = 18.sp,
            color = Blue,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Ми запропонуємо тобі людей поруч.",
            fontSize = 14.sp,
            color = GrayDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            placeholder = { Text("Почни вводити назву...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = GrayMedium,
                focusedBorderColor = Blue,
                cursorColor = Blue
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Що будеш шукати?",
            fontSize = 18.sp,
            color = Blue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        val purposes = listOf("Серйозні стосунки", "Дружнє спілкування", "Вирішу, коли зустрінусь")
        purposes.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPurpose.value == option,
                    onClick = { selectedPurpose.value = option },
                    colors = RadioButtonDefaults.colors(selectedColor = Red)
                )
                Text(option, fontSize = 16.sp, color = Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "З ким хочеш познайомитися?",
            fontSize = 18.sp,
            color = Blue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        val genders = listOf("Жінки", "Чоловіки", "Усі")
        genders.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedGender.value == option,
                    onClick = { selectedGender.value = option },
                    colors = RadioButtonDefaults.colors(selectedColor = Red)
                )
                Text(option, fontSize = 16.sp, color = Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Яке у тебе хобі?",
            fontSize = 18.sp,
            color = Blue,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Обери до 5 хобі, щоб знайти спільні інтереси з іншими.",
            fontSize = 14.sp,
            color = GrayDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Обрані хобі з’являться тут",
            fontSize = 14.sp,
            color = Blue,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        hobbyCategories.forEach { (category, hobbies) ->
            Text(
                text = category,
                fontSize = 16.sp,
                color = Blue,
                fontWeight = FontWeight.SemiBold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
//                mainAxisSpacing = 8.dp,
//                crossAxisSpacing = 8.dp
            ) {
                hobbies.forEach { hobby ->
                    val isSelected = selectedHobbies.contains(hobby)
                    Box(
                        modifier = Modifier
                            .border(
                                1.dp,
                                if (isSelected) Red else GrayMedium,
                                RoundedCornerShape(50)
                            )
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                if (isSelected) {
                                    selectedHobbies.remove(hobby)
                                } else if (selectedHobbies.size < 5) {
                                    selectedHobbies.add(hobby)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = hobby + " +",
                            color = if (isSelected) Red else GrayDark,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = { /* TODO: navigate next */ },
                containerColor = Red
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Далі", tint = White)
            }
        }
    }
}
