package com.example.razomua.model
import kotlinx.serialization.Serializable
import java.time.LocalDate

enum class Gender {
    MALE, FEMALE, OTHER
}
@Serializable
data class User(
    val id: Long,
    val name: String,
    val gender: Gender,
    val birthday: String,
    val email: String
)


