package com.example.razomua.data.network

import com.example.razomua.model.Profile
import com.example.razomua.model.User
import com.example.razomua.model.Swipe
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// DTO класи для парсингу
@Serializable
data class UserDTO(
    val id: Int,
    val name: String? = null,
    val email: String,
    val gender: String? = null,
    val birthday: String? = null,
    val password: String,
)

@Serializable
data class ProfileDTO(
    val id: Int,
    val userId: Int,
    val photoUrl: String? = null,
    val location: String? = null
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
            birthday = dto.birthday,
            password = dto.password,
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
