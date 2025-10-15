package com.example.razomua.network

import com.example.razomua.model.Swipe
import retrofit2.Response
import retrofit2.http.*

interface SwipeApiService {

    @POST("swipes")
    suspend fun sendSwipe(@Body swipe: Swipe): Response<Swipe>

    @GET("swipes/user/{id}")
    suspend fun getUserSwipes(@Path("id") id: Long): Response<List<Swipe>>

    @GET("swipes/matches/{userId}")
    suspend fun getMatches(@Path("userId") userId: Long): Response<List<Swipe>>
}
