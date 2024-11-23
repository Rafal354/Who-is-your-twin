package com.example.whosyourtwin

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class Activity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var photoUri: Uri
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var cameraManager: CameraManager
    private lateinit var imageUploadManager: ImageUploadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val captureButton: Button = findViewById(R.id.captureButton)
        val sendRequestButton: Button = findViewById(R.id.sendRequestButton)

        permissionsManager = PermissionsManager(this)
        cameraManager = CameraManager(this)
        imageUploadManager = ImageUploadManager()

        captureButton.setOnClickListener {
            if (permissionsManager.allPermissionsGranted()) {
                val resultsTextView = findViewById<TextView>(R.id.resultsTextView)
                resultsTextView.visibility = View.INVISIBLE
                photoUri = cameraManager.createImageFileUri()
                cameraManager.cameraLauncher.launch(photoUri)
            } else {
                permissionsManager.requestPermissions()
            }
        }

        sendRequestButton.setOnClickListener {
            val inputStream = getInputStreamFromUri(photoUri)
            if (inputStream != null) {
                imageUploadManager.uploadImageToServer(inputStream, this) { result ->
                    val resultsTextView = findViewById<TextView>(R.id.resultsTextView)
                    resultsTextView.text = result
                    resultsTextView.visibility = View.VISIBLE
                }
            } else {
                Log.e(TAG, "No photo captured yet or failed to retrieve input stream")
            }
        }
    }

    private fun getInputStreamFromUri(uri: Uri): InputStream? {
        return try {
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting InputStream from Uri: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "Activity"
    }
}
