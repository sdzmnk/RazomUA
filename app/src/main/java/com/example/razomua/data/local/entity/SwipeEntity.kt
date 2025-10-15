package com.example.razomua.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.razomua.model.SwipeAction
import java.util.Date

@Entity(tableName = "swipes")
data class SwipeEntity(
    @PrimaryKey val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val action: SwipeAction,
    val createdAt: Date
)
