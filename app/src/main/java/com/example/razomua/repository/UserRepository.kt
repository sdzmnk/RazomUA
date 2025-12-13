//
//package com.example.razomua.repository
//
//import com.example.razomua.data.local.dao.UserDao
//import com.example.razomua.data.local.mapper.toDomain
//import com.example.razomua.data.local.mapper.toEntity
//import com.example.razomua.model.User
//import com.example.razomua.network.UserApiService
//import com.example.razomua.network.RetrofitInstance
//import kotlinx.coroutines.withContext
//import kotlinx.coroutines.Dispatchers
//
//
//class UserRepository(
//    private val userDao: UserDao,
//    private val api: UserApiService = RetrofitInstance.apiUser,
//    ) {
//
//    suspend fun getAllUsersLocal(): List<User> {
//        return userDao.getAllUsers().map { it.toDomain() }
//    }
//
//
//    suspend fun getUserLocalFirst(id: Int): User? {
//        userDao.getUserById(id)?.toDomain()?.let { return it }
//
//
//        val result = getUserFromApi(id)
//        result.getOrNull()?.let { userDao.insert(it.toEntity()) }
//        return result.getOrNull()
//    }
//
//
//    suspend fun getUserFromApi(id: Int): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.getUser(id)
//            if (response.isSuccessful) {
//                response.body()?.let { Result.success(it) }
//                    ?: Result.failure(Exception("Користувач не знайдений"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
//        }
//    }
//
//
//    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.createUser(user)
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    userDao.insert(it.toEntity())
//                    Result.success(it)
//                } ?: Result.failure(Exception("Не вдалося створити користувача"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
//        }
//    }
//
//
//    suspend fun updateUser(id: Int, user: User): Result<User> = withContext(Dispatchers.IO) {
//        try {
//            val response = api.updateUser(id, user)
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    userDao.insert(it.toEntity())
//                    Result.success(it)
//                } ?: Result.failure(Exception("Не вдалося оновити користувача"))
//            } else {
//                Result.failure(Exception(handleApiError(response.code())))
//            }
//        } catch (e: Exception) {
//            Result.failure(Exception("Network error: ${e.localizedMessage}", e))
//        }
//    }
//
//
//    suspend fun refreshUsersFromServer() = withContext(Dispatchers.IO) {
//        try {
//            val response = api.getAllUsers()
//            if (response.isSuccessful) {
//                response.body()?.forEach { user ->
//                    userDao.insert(user.toEntity())
//                }
//            }
//        } catch (e: Exception) {
//
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
