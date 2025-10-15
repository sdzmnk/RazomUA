package com.example.razomua.data.local.mapper

import com.example.razomua.data.local.entity.ProfileEntity
import com.example.razomua.model.Profile

fun Profile.toEntity() = ProfileEntity(
    id = id,
    userId = userId,
    photoUrl = photoUrl,
    location = location
)

fun ProfileEntity.toDomain() = Profile(
    id = id,
    userId = userId,
    photoUrl = photoUrl,
    location = location
)
