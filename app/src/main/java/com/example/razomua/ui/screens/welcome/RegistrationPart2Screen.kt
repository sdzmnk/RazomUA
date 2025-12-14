package com.example.razomua.ui.screens.welcome

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.razomua.repository.ImageRepository
import com.example.razomua.ui.theme.Montserrat
import com.example.razomua.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun RegistrationPart2Screen(
    navController: NavController,
    registerViewModel: RegisterViewModel
) {
    val selectedGender = remember { mutableStateOf<String?>(null) }
    val day = remember { mutableStateOf("") }
    val month = remember { mutableStateOf("") }
    val year = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val uploadedImageUrl = remember { mutableStateOf<String?>(null) }
    val isUploading = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageRepository = remember { ImageRepository() }
    val isFormValid =
        selectedGender.value != null &&
                day.value.isNotBlank() &&
                month.value.isNotBlank() &&
                year.value.isNotBlank() &&
                uploadedImageUrl.value != null &&
                !isUploading.value

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri.value = it
            isUploading.value = true

            coroutineScope.launch {
                val imageUrl = imageRepository.uploadImage(it, context)

                if (imageUrl != null) {
                    uploadedImageUrl.value = imageUrl
                    Log.d("RegistrationPart2", "Image uploaded: $imageUrl")
                } else {
                    Log.e("RegistrationPart2", "Failed to upload image")
                }
                isUploading.value = false
            }
        }
    }

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
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Розкажи про себе — це допоможе створити профіль.",
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = Montserrat,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ваш гендер?",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium,
            fontFamily = Montserrat,
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
            fontWeight = FontWeight.Medium,
            fontFamily = Montserrat,
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
                modifier = Modifier.width(90.dp).height(56.dp),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = month.value,
                onValueChange = { month.value = it },
                placeholder = { Text("Місяць") },
                modifier = Modifier.width(90.dp).height(56.dp),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = year.value,
                onValueChange = { year.value = it },
                placeholder = { Text("Рік") },
                modifier = Modifier.width(90.dp).height(56.dp),
                shape = RoundedCornerShape(50)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Додай свою фотографію",
            fontSize = 18.sp,
            color = Color(0xFF1A1A9E),
            fontWeight = FontWeight.Medium,
            fontFamily = Montserrat,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = !isUploading.value) {
                    launcher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                isUploading.value -> {
                    CircularProgressIndicator()
                }
                selectedImageUri.value != null -> {
                    AsyncImage(
                        model = selectedImageUri.value,
                        contentDescription = "Selected photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Додати фото",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = {
                    val birthday = "${day.value}.${month.value}.${year.value}"
                    val uid = registerViewModel.auth.currentUser?.uid ?: return@Button

                    val userMap = hashMapOf(
                        "gender" to selectedGender.value!!,
                        "birthday" to birthday,
                        "photoUrl" to uploadedImageUrl.value!!
                    )

                    registerViewModel.chatRepository.saveUserData(uid, userMap)
                        .addOnSuccessListener {
                            navController.navigate("register3")
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "RegistrationPart2",
                                "Error saving user data: ${e.localizedMessage}"
                            )
                        }
                },
                enabled = isFormValid,
                shape = CircleShape,
                modifier = Modifier
                    .size(70.dp)
                    .padding(bottom = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFormValid) Color(0xFF1A1A9E) else Color.LightGray,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Далі",
                    tint = Color.White
                )
            }
        }


        Spacer(modifier = Modifier.height(40.dp))
    }
}