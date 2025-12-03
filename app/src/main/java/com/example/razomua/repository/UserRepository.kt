//package com.example.razomua.repository
//
//import com.example.razomua.data.local.dao.UserDao
//import com.example.razomua.data.local.mapper.toDomain
//import com.example.razomua.data.local.mapper.toEntity
//import com.example.razomua.model.User
//import com.example.razomua.network.UserApiService
//import com.example.razomua.network.RetrofitInstance
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class UserRepository(
//    private val userDao: UserDao,                   // локальна база
//    private val api: UserApiService = RetrofitInstance.apiUser // мережа
//) {
//
//    // Offline-first
//
//    suspend fun getUserLocalFirst(id: Long): User? {
//        val localUser = userDao.getUserById(id)?.toDomain()
//        if (localUser != null) return localUser
//
//        val result = getUser(id)
//        result.getOrNull()?.let { userDao.insert(it.toEntity()) }
//        return result.getOrNull()
//    }
//
//    // Методи роботи з API
//    suspend fun getUser(id: Long): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.getUser(id)
//            if (response.isSuccessful) {
//                response.body()?.let { Result.success(it) }
//                    ?: Result.failure(Exception("Користувач не знайдений"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}"))
//        }
//    }
//
//    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.createUser(user)
//            if (response.isSuccessful) {
//                response.body()?.let { Result.success(it) }
//                    ?: Result.failure(Exception("Не вдалося створити користувача"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}"))
//        }
//    }
//
//    suspend fun updateUser(id: Long, user: User): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.updateUser(id, user)
//            if (response.isSuccessful) {
//                response.body()?.let { Result.success(it) }
//                    ?: Result.failure(Exception("Не вдалося оновити користувача"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}"))
//        }
//    }
//
//    // Оновлення локальної бази через сервер (для Worker)
//    suspend fun refreshUsersFromServer() {
//        val response = api.getAllUsers()
//        if (response.isSuccessful) {
//            response.body()?.forEach {
//                userDao.insert(it.toEntity())
//            }
//        }
//    }
//
//    private fun handleApiError(code: Int): String {
//        return when (code) {
//            400 -> "Невірні дані користувача"
//            401 -> "Неавторизовано"
//            404 -> "Користувача не знайдено"
//            500 -> "Помилка сервера"
//            else -> "Невідома помилка ($code)"
//        }
//    }
//}


package com.example.razomua.repository

import com.example.razomua.data.local.dao.UserDao
import com.example.razomua.data.local.mapper.toDomain
import com.example.razomua.data.local.mapper.toEntity
import com.example.razomua.model.User
import com.example.razomua.network.UserApiService
import com.example.razomua.network.RetrofitInstance
import kotlinx.coroutines.withContext
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRepository(
    private val userDao: UserDao,
    private val api: UserApiService = RetrofitInstance.apiUser,
    ) {

    suspend fun getAllUsersLocal(): List<User> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    // Offline-first: сначала локальная база
    suspend fun getUserLocalFirst(id: Int): User? {
        userDao.getUserById(id)?.toDomain()?.let { return it }

        // если нет — с API
        val result = getUserFromApi(id)
        result.getOrNull()?.let { userDao.insert(it.toEntity()) }
        return result.getOrNull()
    }

    // Получение одного пользователя с API
    suspend fun getUserFromApi(id: Int): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUser(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Користувач не знайдений"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
        }
    }

    // Создание пользователя через API
    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.createUser(user)
            if (response.isSuccessful) {
                response.body()?.let {
                    // сохраняем в локальную базу
                    userDao.insert(it.toEntity())
                    Result.success(it)
                } ?: Result.failure(Exception("Не вдалося створити користувача"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
        }
    }

    // Обновление пользователя
    suspend fun updateUser(id: Int, user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateUser(id, user)
            if (response.isSuccessful) {
                response.body()?.let {
                    userDao.insert(it.toEntity()) // обновляем локально
                    Result.success(it)
                } ?: Result.failure(Exception("Не вдалося оновити користувача"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
        }
    }

    // Обновление всех пользователей с сервера
    suspend fun refreshUsersFromServer() = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllUsers()
            if (response.isSuccessful) {
                response.body()?.forEach { user ->
                    userDao.insert(user.toEntity())
                }
            }
        } catch (e: Exception) {
            // можно логировать ошибку, но не падаем
        }
    }

    private fun handleApiError(code: Int): String {
        return when (code) {
            400 -> "Невірні дані користувача"
            401 -> "Неавторизовано"
            404 -> "Користувача не знайдено"
            500 -> "Помилка сервера"
            else -> "Невідома помилка ($code)"
        }
    }
}
