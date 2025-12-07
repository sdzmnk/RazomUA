package com.example.razomua.model

data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0,
    val isCurrentUser: Boolean = false
)