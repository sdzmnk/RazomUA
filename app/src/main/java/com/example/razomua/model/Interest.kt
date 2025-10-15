package com.example.razomua.model

import kotlinx.serialization.Serializable

@Serializable
data class Interest(
    val id: Long,
    val name: String
)
