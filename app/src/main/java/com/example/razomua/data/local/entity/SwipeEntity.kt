package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "swipes")
data class SwipeEntity(
    @PrimaryKey val id: Int,
    val fromUserId: Int,
    val toUserId: Int,
    val action: String,
    val createdAt: String
)
