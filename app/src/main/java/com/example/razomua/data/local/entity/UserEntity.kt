package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.razomua.model.Gender
import java.time.LocalDate

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val gender: Gender,
    val birthday: String,
    val email: String
)
