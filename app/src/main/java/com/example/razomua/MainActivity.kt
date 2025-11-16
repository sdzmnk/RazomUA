package com.example.razomua

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.razomua.navigation.AppNavHost
import com.example.razomua.ui.screens.welcome.WelcomeScreen
import com.example.razomua.ui.theme.RazomUATheme
import com.example.websocketchatapp.WebSocketViewModel
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.example.razomua.data.local.AppDatabase
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.repository.UserRepository
import com.example.razomua.ui.screens.welcome.WelcomeScreen
import com.example.razomua.ui.theme.RazomUATheme
import com.example.razomua.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DatabaseProvider.getDatabase(this)

        val repository = UserRepository(db.userDao())

        CoroutineScope(Dispatchers.IO).launch {

            val users = repository.getAllUsersLocal()
            users.forEach { Log.d("DB_TEST2", it.toString()) }
        }

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