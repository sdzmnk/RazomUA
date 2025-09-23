package com.example.razomua.model

import java.time.LocalDate

enum class Gender {
    MALE, FEMALE, OTHER
}

data class User(
    val id: Long,
    val name: String,
    val gender: Gender,
    val birthday: LocalDate?,
    val email: String
)


