package com.example.razomua.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.razomua.ui.screens.login.LoginScreen
import com.example.razomua.ui.screens.register.RegisterScreen
import com.example.razomua.ui.screens.welcome.ChatScreen
import com.example.razomua.ui.screens.welcome.DiagramScreen
import com.example.razomua.ui.screens.welcome.GoogleMapsScreen
import com.example.razomua.ui.screens.welcome.ProfileScreen
import com.example.razomua.ui.screens.welcome.SwipeScreen
import com.example.razomua.ui.screens.welcome.WelcomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") { RegisterScreen() }
        composable("swipe") {
            SwipeScreen(navController = navController)
        }
        composable("chat") {
            ChatScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("googlemaps") {
            GoogleMapsScreen(navController = navController)
        }
        composable("diagram") {
            DiagramScreen(navController = navController)
        }
    }
}
