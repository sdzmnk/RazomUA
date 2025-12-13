package com.example.razomua.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.razomua.model.ImgBBResponse
import com.example.razomua.network.ImgBBApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ImageRepository {

    // Получите свой API ключ на https://api.imgbb.com/
    private val apiKey = "2c1a44cd8f3f5c7f34d5daf2a1e5dc80"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.imgbb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ImgBBApi::class.java)

    suspend fun uploadImage(uri: Uri, context: Context): String? {
        return try {
            val file = uriToFile(uri, context)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = api.uploadImage(apiKey, body)

            // Теперь response - это сам ImgBBResponse
            if (response.success) {
                val imageUrl = response.data?.url
                Log.d("ImageUpload", "Success: $imageUrl")

                // Удаляем временный файл
                file.delete()

                imageUrl
            } else {
                Log.e("ImageUpload", "Upload failed: status=${response.status}")
                file.delete()
                null
            }
        } catch (e: Exception) {
            Log.e("ImageUpload", "Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}