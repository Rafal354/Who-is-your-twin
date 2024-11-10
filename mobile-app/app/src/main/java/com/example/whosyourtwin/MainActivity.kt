package com.example.whosyourtwin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var photoUri: Uri
    private lateinit var savedImageFile: File
    private val outputDirectory: File by lazy {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d(TAG, "Image captured successfully: $photoUri")

                imageView.setImageURI(photoUri)
            } else {
                Log.e(TAG, "Image capture failed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val captureButton: Button = findViewById(R.id.captureButton)
        val sendRequestButton: Button = findViewById(R.id.sendRequestButton)

        // Capture button click listener
        captureButton.setOnClickListener {
            if (allPermissionsGranted()) {
                photoUri = createImageFileUri()
                cameraLauncher.launch(photoUri)
            } else {
                requestPermissions()
            }
        }

        // Send request button click listener
        sendRequestButton.setOnClickListener {
            val filepath = photoUri.path
            val sanitizedPath = Uri.encode(filepath)

            if (sanitizedPath != null) {
                uploadImageToServer(sanitizedPath)
            } else {
                Log.e(TAG, "No photo captured yet")
            }
        }

        if (allPermissionsGranted()) {
            photoUri = createImageFileUri()
            cameraLauncher.launch(photoUri)
        } else {
            requestPermissions()
        }
    }

    private fun uploadImageToServer(filepath: String) {

//        println("File path: $filepath")
//
//        val imageFile = File(filepath)
//
//        // Check if the file exists before proceeding
//        if (!imageFile.exists()) {
//            Log.e(TAG, "Image file not found at path: $filepath")
//            return
//        }

        // Prepare the request body for the file (image/jpeg or appropriate MIME type)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), savedImageFile)
        val body = MultipartBody.Part.createFormData("image", savedImageFile.name, requestFile)

        // Make the request to the Flask server
        RetrofitClient.instance.uploadImage(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // The server returns the processed image as a ResponseBody
                    val inputStream = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageView.setImageBitmap(bitmap) // Set the processed image to the ImageView
                } else {
                    Log.e(TAG, "Request failed with status: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Failed to process image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Request failed: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestMultiplePermissionsLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (allPermissionsGranted()) {
                photoUri = createImageFileUri()
                cameraLauncher.launch(photoUri)
            } else {
                Log.e(TAG, "Permissions not granted")
            }
        }

    private fun createImageFileUri(): Uri {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.ITALY).format(Date())
        val imageFile = File(outputDirectory, "$timeStamp.jpg")
        savedImageFile = imageFile
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)
    }

    companion object {
        private const val TAG = "CameraApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH_mm_ss-SSS"
        private val REQUIRED_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET)
    }
}
