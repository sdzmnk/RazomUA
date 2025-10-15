package com.example.razomua.model
import kotlinx.serialization.Serializable
@Serializable
data class Profile(
    val id: Long,
    val userId: Long,
    val photoUrl: String?,
    val location: String?
)
