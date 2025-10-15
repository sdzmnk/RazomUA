package com.example.razomua.repository

import com.example.razomua.data.local.dao.SwipeDao
import com.example.razomua.data.local.mapper.toDomain
import com.example.razomua.data.local.mapper.toEntity
import com.example.razomua.model.Swipe
import com.example.razomua.network.SwipeApiService
import com.example.razomua.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SwipeRepository(
    private val swipeDao: SwipeDao,                      // локальна база
    private val api: SwipeApiService = RetrofitInstance.apiSwipe // мережа
) {

    // Offline-first: спершу локальна база, потім API
    suspend fun getUserSwipesLocalFirst(userId: Long): List<Swipe> {

        val localSwipes = swipeDao.getSwipesFromUser(userId).map { it.toDomain() }
        if (localSwipes.isNotEmpty()) return localSwipes


        val result = getUserSwipesFromApi(userId)
        result.getOrNull()?.forEach { swipeDao.insert(it.toEntity()) }
        return result.getOrNull() ?: emptyList()
    }

    // API
    suspend fun getUserSwipesFromApi(userId: Long): Result<List<Swipe>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserSwipes(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun sendSwipe(swipe: Swipe): Result<Swipe> = withContext(Dispatchers.IO) {
        try {
            val response = api.sendSwipe(swipe)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Не вдалося відправити свайп"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun getUserSwipes(id: Long): Result<List<Swipe>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserSwipes(id)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun getMatches(userId: Long): Result<List<Swipe>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMatches(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    // Оновлення локальної бази через сервер (для Worker)
    suspend fun refreshSwipesFromServer(userId: Long) {
        val result = getUserSwipesFromApi(userId)
        result.getOrNull()?.forEach { swipeDao.insert(it.toEntity()) }
    }

    private fun handleApiError(code: Int): String {
        return when (code) {
            400 -> "Невірні дані"
            401 -> "Неавторизовано"
            404 -> "Не знайдено"
            500 -> "Помилка сервера"
            else -> "Невідома помилка ($code)"
        }
    }
}
