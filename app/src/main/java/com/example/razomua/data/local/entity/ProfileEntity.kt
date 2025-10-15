package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: Long,
    val userId: Long,
    val photoUrl: String?,
    val location: String?
)
