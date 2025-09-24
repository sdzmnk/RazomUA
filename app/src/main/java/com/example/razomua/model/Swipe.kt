package com.example.razomua.model

import java.util.Date

enum class SwipeAction {
    LIKE,
    DISLIKE
}

data class Swipe(
    val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val action: SwipeAction,
    val createdAt: Date
)


