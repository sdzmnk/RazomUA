package com.example.razomua.network

import com.example.razomua.model.ImgBBResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ImgBBApi {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): ImgBBResponse
}