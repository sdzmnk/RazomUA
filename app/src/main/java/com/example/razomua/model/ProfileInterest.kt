package com.example.razomua.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfileInterest(
    val profileId: Long,
    val interestId: Long
)
