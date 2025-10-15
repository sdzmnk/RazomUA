package com.example.razomua.network

import com.example.razomua.model.User
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("users")
    suspend fun getAllUsers(): Response<List<User>>


    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @POST("users")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): Response<User>
}
