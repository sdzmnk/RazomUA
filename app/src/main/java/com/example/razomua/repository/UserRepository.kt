package com.example.razomua.repository

import com.example.razomua.model.User
import com.example.razomua.network.UserApiService
import com.example.razomua.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {

    private val api = RetrofitInstance.apiUser

    suspend fun getUser(id: Long): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUser(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Користувач не знайдений"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.createUser(user)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Не вдалося створити користувача"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun updateUser(id: Long, user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateUser(id, user)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Не вдалося оновити користувача"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
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
