package com.example.razomua.model

data class ChatUser(
    val id: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val avatarUrl: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0,
    val imageRes: Int = 0
)