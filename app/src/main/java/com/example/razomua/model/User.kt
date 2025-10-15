package com.example.razomua.model
import kotlinx.serialization.Serializable

enum class Gender {
    MALE, FEMALE, OTHER
}
@Serializable
data class User(
    val id: Long,
    val name: String,
    val gender: String,
    val birthday: String?,
    val email: String
)


