package com.example.razomua.model
import kotlinx.serialization.Serializable

enum class Gender {
    MALE, FEMALE, OTHER
}
@Serializable
data class User(
    val id: Int,
    val name: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val email: String,
    val password: String
)


