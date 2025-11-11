package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val email: String,
    val password: String
)
