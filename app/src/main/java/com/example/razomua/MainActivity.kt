package com.example.razomua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.razomua.ui.screens.welcome.WelcomeScreen
import com.example.razomua.ui.theme.RazomUATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RazomUATheme {
                WelcomeScreen()
            }
        }
    }
}