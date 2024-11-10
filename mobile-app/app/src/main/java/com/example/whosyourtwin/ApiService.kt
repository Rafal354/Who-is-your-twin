package com.example.whosyourtwin

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class PhotoInfoRequest(val filepath: String)

data class PhotoInfoResponse(
    val input_filepath: String,
    val output_filepath: String,
    val info: PhotoInfo
)

data class PhotoInfo(
    val description: String,
    val dimensions: String,
    val format: String,
    val size_kb: Int
)

interface ApiService {
    @Multipart
    @POST("/process_photo")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}
