package com.example.razomua

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        // Создаём базу
        val db = DatabaseProvider.getDatabase(this)

        // Создаём репозиторий
        val repository = UserRepository(db.userDao())

        // Тест: добавляем пользователя и выводим всех
        CoroutineScope(Dispatchers.IO).launch {
            // Добавляем тестового пользователя
            val testUser = UserEntity(
                id = 1,
                name = "Sofiia",
                gender = "FEMALE",
                birthday = "2000-01-01",
                email = "sofiia@example.com",
                password = "12346"
            )
            db.userDao().insert(testUser)

            // Получаем всех пользователей и выводим в Logcat
            val users = repository.getAllUsersLocal()
            users.forEach { Log.d("DB_TEST", it.toString()) }
        }

        enableEdgeToEdge()
        setContent {
            RazomUATheme {
                com.example.razomua.navigation.AppNavHost()
            }
        }
    }
}