package com.example.razomua.data.network

import com.example.razomua.model.Profile
import com.example.razomua.model.User
import com.example.razomua.model.Swipe
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// DTO класи для парсингу
@Serializable
data class UserDTO(
    val id: Long,
    val name: String,
    val email: String,
    val gender: String,
    val birthday: String? = null
)

@Serializable
data class ProfileDTO(
    val id: Long,
    val userId: Long,
    val photoUrl: String? = null,
    val location: String? = null
)

@Serializable
data class SwipeDTO(
    val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val action: String,
    val createdAt: String
)

// Функції парсингу
fun parseUsersJson(jsonString: String): List<User> {
    val dtoList = Json.decodeFromString<List<UserDTO>>(jsonString)
    return dtoList.map { dto ->
        User(
            id = dto.id,
            name = dto.name,
            email = dto.email,
            gender = dto.gender,
            birthday = dto.birthday
        )
    }
}

fun parseProfilesJson(jsonString: String): List<Profile> {
    val dtoList = Json.decodeFromString<List<ProfileDTO>>(jsonString)
    return dtoList.map { dto ->
        Profile(
            id = dto.id,
            userId = dto.userId,
            photoUrl = dto.photoUrl,
            location = dto.location
        )
    }
}

fun parseSwipesJson(jsonString: String): List<Swipe> {
    val dtoList = Json.decodeFromString<List<SwipeDTO>>(jsonString)
    return dtoList.map { dto ->
        Swipe(
            id = dto.id,
            fromUserId = dto.fromUserId,
            toUserId = dto.toUserId,
            action = dto.action,
            createdAt = dto.createdAt
        )
    }
}
