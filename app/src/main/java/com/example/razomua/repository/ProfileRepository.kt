package com.example.razomua.repository

import com.example.razomua.model.Profile
import com.example.razomua.network.ProfileApiService
import com.example.razomua.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository {

    private val api = RetrofitInstance.apiProfile

    suspend fun getProfiles(): Result<List<Profile>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllProfiles()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun getProfileById(id: Long): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProfile(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Профіль не знайдено"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    suspend fun createProfile(profile: Profile): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val response = api.createProfile(profile)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Не вдалося створити профіль"))
            } else {
                Result.failure(Exception(handleApiError(response.code())))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    private fun handleApiError(code: Int): String {
        return when (code) {
            400 -> "Невірні дані профілю"
            401 -> "Неавторизовано"
            404 -> "Профіль не знайдено"
            500 -> "Помилка сервера"
            else -> "Невідома помилка ($code)"
        }
    }
}
