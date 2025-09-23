package com.example.razomua.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.razomua.ui.screens.login.LoginScreen
import com.example.razomua.ui.screens.register.RegisterScreen
import com.example.razomua.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen() }
        composable("register") { RegisterScreen() }
    }
}
