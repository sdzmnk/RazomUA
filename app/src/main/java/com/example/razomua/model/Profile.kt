package com.example.razomua.model
import kotlinx.serialization.Serializable
@Serializable
data class Profile(
    val id: Int,
    val userId: Int,
    val photoUrl: String?,
    val location: String?
)
