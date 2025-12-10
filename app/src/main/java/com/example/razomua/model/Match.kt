package com.example.razomua.model

data class Match(
    val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val chatId: String = "",
    val timestamp: Long = 0,
    val isNew: Boolean = true
)
