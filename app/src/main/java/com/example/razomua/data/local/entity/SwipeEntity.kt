package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "swipes")
data class SwipeEntity(
    @PrimaryKey val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val action: String,
    val createdAt: String
)
