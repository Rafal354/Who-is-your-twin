package com.example.whosyourtwin

import android.content.Context
import android.os.Parcelable
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream

class ImageUploadManager {

    fun uploadImageToServer(
        inputStream: InputStream,
        context: Context,
        callback: (ArrayList<out Parcelable>?) -> Unit
    ) {
        try {
            val tempFile = File(context.cacheDir, "temp_image.jpg")
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), tempFile)
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        if (!responseBody.isNullOrEmpty()) {
                            try {
                                val jsonResponse = JSONObject(responseBody)
                                val resultList = mutableListOf<NamePercentage>()

                                jsonResponse.keys().forEach {
                                    val value = jsonResponse.getDouble(it)
                                    val name = it
                                    val percentage = (value * 100).toInt()

                                    resultList.add(NamePercentage(name, percentage))
                                }
                                callback(ArrayList(resultList))

                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to parse JSON: ${e.message}")
                                callback(ArrayList(emptyList()))
                            }
                        }
                    } else {
                        Log.e(TAG, "Request failed with status: ${response.code()}")
                        callback(ArrayList(emptyList()))
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Request failed: ${t.message}")
                    callback(ArrayList(emptyList()))
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error during image upload: ${e.message}")
            callback(ArrayList(emptyList()))
        }
    }

    companion object {
        private const val TAG = "ImageUploadManager"
    }
}
