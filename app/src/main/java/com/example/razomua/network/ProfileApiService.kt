package com.example.razomua.network

import com.example.razomua.model.Profile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Body

interface ProfileApiService {

    @GET("profiles")
    suspend fun getAllProfiles(): Response<List<Profile>>

    @GET("profiles/{id}")
    suspend fun getProfile(@Path("id") id: Long): Response<Profile>

    @POST("profiles")
    suspend fun createProfile(@Body profile: Profile): Response<Profile>
}
