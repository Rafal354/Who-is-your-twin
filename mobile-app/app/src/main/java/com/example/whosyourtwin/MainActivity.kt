package com.example.whosyourtwin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var imageInfoTextView: TextView
    private lateinit var photoUri: Uri
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
                displayImageInfo()
            } else {
                Log.e(TAG, "Image capture failed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (allPermissionsGranted()) {
            photoUri = createImageFileUri()
            cameraLauncher.launch(photoUri)
        } else {
            requestPermissions()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageInfoTextView = findViewById(R.id.imageInfoTextView)
        val captureButton: Button = findViewById(R.id.captureButton)

        captureButton.setOnClickListener {
            if (allPermissionsGranted()) {
                photoUri = createImageFileUri()
                cameraLauncher.launch(photoUri)
            } else {
                requestPermissions()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
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
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        val imageFile = File(outputDirectory, "$timeStamp.jpg")
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)

    }


    private fun displayImageInfo() {
        try {
            val inputStream = contentResolver.openInputStream(photoUri)

            val fileSizeInBytes = inputStream?.available()?.toLong() ?: 0L
            val fileSizeInKB = fileSizeInBytes / 1024

            val imageInfo = "File size: $fileSizeInKB KB"
            imageInfoTextView.text = imageInfo

            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Error accessing image info: ${e.message}")
        }
    }


    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH_mm_ss-SSS"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


}