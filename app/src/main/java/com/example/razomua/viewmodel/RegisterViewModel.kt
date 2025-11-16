package com.example.razomua.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.data.local.DatabaseProvider
import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.model.User
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    // DAO для роботи з Room
    private val userDao = DatabaseProvider.getDatabase(application).userDao()

    // Список користувачів для UI
    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    // Тимчасове збереження даних під час реєстрації (між екранами)
    var tempUser: User? = null

    /** Початок реєстрації: зберігаємо email та пароль тимчасово */
    fun startRegistration(email: String, password: String) {
        tempUser = User(
            id = 0, // Room згенерує id
            email = email,
            password = password
        )
    }

    /** Завершення реєстрації: додаємо ім'я, гендер, день народження і зберігаємо в Room */
    fun completeRegistration(name: String, gender: String?, birthday: String?) {
        tempUser?.let { user ->
            val fullUser = user.copy(
                name = name,
                gender = gender,
                birthday = birthday
            )

            // Зберігаємо в Room
            viewModelScope.launch {
                val userEntity = UserEntity(
                    id = 0, // Room автоматично згенерує id
                    name = fullUser.name,
                    gender = fullUser.gender,
                    birthday = fullUser.birthday,
                    email = fullUser.email,
                    password = fullUser.password
                )
                userDao.insert(userEntity)

                // Оновлюємо локальний список користувачів для UI
                loadAllUsers()
            }

            tempUser = null
        }
    }

    /** Завантаження всіх користувачів із бази даних */
    fun loadAllUsers() {
        viewModelScope.launch {
            val allUsers = userDao.getAllUsers()
            _users.clear()
            _users.addAll(allUsers.map { userEntity ->
                User(
                    id = userEntity.id,
                    name = userEntity.name,
                    gender = userEntity.gender,
                    birthday = userEntity.birthday,
                    email = userEntity.email,
                    password = userEntity.password
                )
            })
        }
    }

    fun logAllUsers() {
        viewModelScope.launch {
            val users = userDao.getAllUsers()
            users.forEach { Log.d("DB_USERS", it.toString()) }
        }
    }


}
