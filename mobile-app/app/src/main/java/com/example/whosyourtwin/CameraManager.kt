package com.example.whosyourtwin

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraManager(private val activity: AppCompatActivity, private val imageViewId: Int) {

    private lateinit var savedImageFile: File
    private val outputDirectory: File by lazy {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        mediaDir ?: activity.filesDir
    }

    val cameraLauncher =
        activity.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d(TAG, "Image captured successfully: $savedImageFile")
                activity.findViewById<ImageView>(imageViewId)
                    .setImageURI(Uri.fromFile(savedImageFile))
            } else {
                Log.e(TAG, "Image capture failed")
            }
        }

    fun createImageFileUri(): Uri {
        val timeStamp = SimpleDateFormat(FILENAME_FORMAT, Locale.ITALY).format(Date())
        val imageFile = File(outputDirectory, "$timeStamp.jpg")
        savedImageFile = imageFile
        return FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.fileprovider",
            imageFile
        )
    }

    companion object {
        private const val TAG = "CameraManager"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH_mm_ss-SSS"
    }
}
