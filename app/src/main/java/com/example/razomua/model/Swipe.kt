package com.example.razomua.model

import kotlinx.serialization.Serializable

enum class SwipeAction {
    LIKE,
    DISLIKE
}

@Serializable
data class Swipe(
    val id: Int,
    val fromUserId: Int,
    val toUserId: Int,
    val action: String,
    val createdAt: String
)
