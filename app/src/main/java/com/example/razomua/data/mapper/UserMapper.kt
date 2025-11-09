package com.example.razomua.data.local.mapper

import com.example.razomua.data.local.entity.UserEntity
import com.example.razomua.model.User

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    gender = gender,
    birthday = birthday,
    email = email,
    password = password
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    gender = gender,
    birthday = birthday,
    email = email,
    password = password
)
