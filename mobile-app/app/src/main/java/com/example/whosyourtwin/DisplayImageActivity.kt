package com.example.whosyourtwin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var photoUri: Uri
    private lateinit var cameraManager: CameraManager
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var imageUploadManager: ImageUploadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        val sendRequestButton: Button = findViewById(R.id.sendRequestToBackendButton)

        permissionsManager = PermissionsManager(this)
        cameraManager = CameraManager(this, R.id.displayImageView)
        imageUploadManager = ImageUploadManager()

        if (permissionsManager.allPermissionsGranted()) {
            photoUri = cameraManager.createImageFileUri()
            cameraManager.cameraLauncher.launch(photoUri)

            Log.d(TAG, "Camera launched with URI: $photoUri")
        } else {
            Log.w(TAG, "Permissions not granted, requesting permissions")
            permissionsManager.requestPermissions()
        }

        sendRequestButton.setOnClickListener {
            Log.i(TAG, "Send Request button clicked")

            val inputStream = getInputStreamFromUri(photoUri)
            if (inputStream != null) {
                Log.d(TAG, "InputStream successfully retrieved from URI")

                imageUploadManager.uploadImageToServer(inputStream, this) { result ->
                    Log.d(TAG, "Image upload completed, result: $result")

                    val intent = Intent(this, DisplayResultActivity::class.java)
                    intent.putParcelableArrayListExtra("result", result)
                    startActivity(intent)
                    finish()

                    Log.i(
                        TAG,
                        "Result sent to DisplayResultActivity and activity finished"
                    )
                }
            } else {
                Log.e(TAG, "No photo captured yet or failed to retrieve input stream")
            }
        }
    }

    private fun getInputStreamFromUri(uri: Uri): InputStream? {
        return try {
            contentResolver.openInputStream(uri).also {
                Log.d(TAG, "Input stream opened successfully for URI: $uri")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting InputStream from Uri: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "DisplayImageActivity"
    }
}
