package com.example.razomua.model

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    @SerializedName("data")
    val data: ImageData?,

    @SerializedName("success")
    val success: Boolean,

    @SerializedName("status")
    val status: Int
)

data class ImageData(
    @SerializedName("url")
    val url: String,

    @SerializedName("display_url")
    val display_url: String,

    @SerializedName("delete_url")
    val delete_url: String?
)