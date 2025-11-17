package com.example.razomua.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.razomua.ui.screens.login.LoginScreen
import com.example.razomua.ui.screens.register.RegisterScreen
import com.example.razomua.ui.screens.welcome.ChatScreen
import com.example.razomua.ui.screens.welcome.DiagramScreen
import com.example.razomua.ui.screens.welcome.GoogleMapsScreen
import com.example.razomua.ui.screens.welcome.PreferencesSettingsScreen
import com.example.razomua.ui.screens.welcome.ProfileScreen
import com.example.razomua.ui.screens.welcome.RegistrationPart2Screen
import com.example.razomua.ui.screens.welcome.SwipeScreen
import com.example.razomua.ui.screens.welcome.ChatScreen
import com.example.razomua.ui.screens.welcome.ListChatsScreen
import com.example.razomua.ui.screens.welcome.NearbyUsersScreen
import com.example.razomua.ui.screens.welcome.WelcomeScreen
import com.example.websocketchatapp.WebSocketViewModel
import com.example.razomua.viewmodel.RegisterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost( webSocketViewModel: WebSocketViewModel) {
    val navController = rememberNavController()
    val registerViewModel: RegisterViewModel = viewModel()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController, registerViewModel = registerViewModel)}
        composable("swipe") {
            SwipeScreen(navController = navController)
        }
        composable("chats") {
            ListChatsScreen(navController = navController, viewModel = webSocketViewModel)
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
        composable("chat") { backStackEntry ->
            ChatScreen(navController = navController, viewModel = webSocketViewModel)
        }


        composable("register2") {
            RegistrationPart2Screen(navController = navController, registerViewModel = registerViewModel)
        }

        composable("register3") {
            PreferencesSettingsScreen(navController = navController)
        }
        composable("nearby") {
            NearbyUsersScreen(
                navController = navController,
                onUserClick = { userId ->
                    navController.navigate("profile/$userId")
                }
            )
        }


    }
}
