package com.example.razomua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.razomua.navigation.AppNavHost
import com.example.razomua.ui.screens.welcome.WelcomeScreen
import com.example.razomua.ui.theme.RazomUATheme
import com.example.websocketchatapp.WebSocketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = WebSocketViewModel()
        setContent {
            RazomUATheme {
                val webSocketViewModel: WebSocketViewModel = viewModel()
                AppNavHost(webSocketViewModel = webSocketViewModel)
//                com.example.razomua.navigation.AppNavHost()
            }
        }
    }
}