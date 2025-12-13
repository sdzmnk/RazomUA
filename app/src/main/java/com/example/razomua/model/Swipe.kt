package com.example.razomua.model


data class Swipe(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val action: String = "",
    val timestamp: Long = 0
)

enum class SwipeAction {
    LIKE,
    DISLIKE
}