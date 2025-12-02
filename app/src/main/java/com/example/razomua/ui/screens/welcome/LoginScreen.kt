//package com.example.razomua.ui.screens.login
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.razomua.ui.theme.Blue
//import com.example.razomua.ui.theme.GrayMedium
//import com.example.razomua.viewmodel.LoginViewModel
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//@Composable
//fun LoginScreen(
//    navController: NavController,
//    viewModel: LoginViewModel = viewModel()
//) {
//    val email by viewModel.email.observeAsState("")
//    val password by viewModel.password.observeAsState("")
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(Unit) {
//        viewModel.events.collectLatest { message ->
//            coroutineScope.launch {
//                snackbarHostState.showSnackbar(message)
//            }
//            if (message == "Логін успішний!") {
//                // Перехід на екран свайпів
//                navController.navigate("swipe") {
//                    popUpTo("login") { inclusive = true } // видаляємо Login зі стеку
//                }
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Column(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = "Введи свої дані, щоб продовжити",
//                    fontSize = 24.sp,
//                    color = Blue,
//                    modifier = Modifier.padding(top = 80.dp, bottom = 40.dp)
//                )
//
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = { viewModel.onEmailChange(it) },
//                    placeholder = { Text("example@gmail.com", color = GrayMedium) },
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .border(1.dp, GrayMedium, CircleShape),
//                    shape = CircleShape,
//                )
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = { viewModel.onPasswordChange(it) },
//                    placeholder = { Text("Пароль", color = GrayMedium) },
//                    visualTransformation = PasswordVisualTransformation(),
//                    singleLine = true,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .border(1.dp, GrayMedium, CircleShape),
//                    shape = CircleShape
//                )
//            }
//
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.End
//            ) {
//                Button(
//                    onClick = { viewModel.login() },
//                    shape = CircleShape,
//                    modifier = Modifier
//                        .padding(bottom = 40.dp)
//                        .size(70.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowForward,
//                        contentDescription = "Login",
//                        tint = Color.White
//                    )
//                }
//            }
//        }
//    }
//}


package com.example.razomua.ui.screens.login

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
import com.example.razomua.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val email by viewModel.email.observeAsState("")
    val password by viewModel.password.observeAsState("")

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Слушаем события (ошибки/успешный логин)
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
            if (message == "Логін успішний!") {
                navController.navigate("swipe") {
                    popUpTo("login") { inclusive = true }
                }
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
                    text = "Введи свої дані, щоб продовжити",
                    fontSize = 24.sp,
                    color = Blue,
                    modifier = Modifier.padding(top = 80.dp, bottom = 40.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
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
                    onValueChange = { viewModel.onPasswordChange(it) },
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
                    onClick = { viewModel.login() },
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
}
