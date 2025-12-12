

package com.example.razomua.ui.screens.register

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.razomua.ui.theme.Blue
import com.example.razomua.ui.theme.GrayMedium
import com.example.razomua.viewmodel.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val isValid = email.isNotBlank() && password.isNotBlank() && name.isNotBlank()

    val registerState by viewModel.registerState.observeAsState()
    val error by viewModel.error.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(registerState, error) {
        error?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }

        if (registerState == true) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Реєстрація успішна!")
            }
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Введи свої дані, щоб зареєструватися",
                    fontSize = 24.sp,
                    color = Blue,
                    modifier = Modifier.padding(top = 80.dp, bottom = 40.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Ім'я", color = GrayMedium) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GrayMedium, CircleShape),
                    shape = CircleShape
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("example@gmail.com", color = GrayMedium) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GrayMedium, CircleShape),
                    shape = CircleShape
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                    onClick = {
                        if (isValid) {
                            viewModel.register(email, password, name)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .size(70.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isValid) Color(0xFFFF4545) else Color.LightGray
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Register",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
