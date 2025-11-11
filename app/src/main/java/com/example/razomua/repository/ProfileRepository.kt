package com.example.razomua.repository

import com.example.razomua.data.local.dao.ProfileDao
import com.example.razomua.data.local.mapper.toDomain
import com.example.razomua.data.local.mapper.toEntity
import com.example.razomua.model.Profile
import com.example.razomua.network.ProfileApiService
import com.example.razomua.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(
    private val profileDao: ProfileDao,                 // локальна база
    private val api: ProfileApiService = RetrofitInstance.apiProfile // мережа
)  {

    // Offline-first:
    suspend fun getProfileLocalFirst(userId: Int): Profile? {
        val localProfile = profileDao.getProfileByUserId(userId)?.toDomain()
        if (localProfile != null) return localProfile

        val result = getProfileFromApi(userId)
        result.getOrNull()?.let { profileDao.insert(it.toEntity()) } // зберігаємо локально
        return result.getOrNull()
    }

    suspend fun getProfileFromApi(userId: Int): Result<Profile> = withContext(Dispatchers.IO) {
        try {
            val response = api.getProfile(userId)
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

    suspend fun getProfileById(id: Int): Result<Profile> = withContext(Dispatchers.IO) {
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

    // Оновлення локальної бази через сервер (для Worker)
    suspend fun refreshProfilesFromServer() {
        val response = api.getAllProfiles()
        if (response.isSuccessful) {
            response.body()?.forEach {
                profileDao.insert(it.toEntity())
            }
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
