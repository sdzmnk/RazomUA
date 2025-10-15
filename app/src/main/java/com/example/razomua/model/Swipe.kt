package com.example.razomua.model

import kotlinx.serialization.Serializable

enum class SwipeAction {
    LIKE,
    DISLIKE
}

@Serializable
data class Swipe(
    val id: Long,
    val fromUserId: Long,
    val toUserId: Long,
    val action: SwipeAction,
    val createdAt: String
)
