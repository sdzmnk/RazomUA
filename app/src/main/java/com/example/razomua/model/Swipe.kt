package com.example.razomua.model

import kotlinx.serialization.Serializable

//enum class SwipeAction {
//    LIKE,
//    DISLIKE
//}
//
//@Serializable
//data class Swipe(
//    val id: Int,
//    val fromUserId: Int,
//    val toUserId: Int,
//    val action: String,
//    val createdAt: String
//)
data class Swipe(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val action: String = "", // "LIKE" or "DISLIKE"
    val timestamp: Long = 0
)

enum class SwipeAction {
    LIKE,
    DISLIKE
}