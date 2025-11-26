package com.example.razomua

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.razomua.navigation.AppNavHost
import com.example.razomua.ui.theme.RazomUATheme
import com.example.websocketchatapp.WebSocketViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.repository.UserRepository

class MainActivity : ComponentActivity() {
    private var isDarkTheme by mutableStateOf(false)
    private lateinit var lightSensorManager: LightSensorManager

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

        lightSensorManager = LightSensorManager(this) { isDark ->
            isDarkTheme = isDark
        }

        setContent {
            RazomUATheme(darkTheme = isDarkTheme) {
                val webSocketViewModel: WebSocketViewModel = viewModel()
                AppNavHost(webSocketViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensorManager.start()
    }

    override fun onPause() {
        super.onPause()
        lightSensorManager.stop()
    }
}
