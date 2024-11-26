package com.example.whosyourtwin

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionsManager(private val activity: AppCompatActivity) {

    private val requiresPermission =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET)

    private val requestMultiplePermissionsLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            if (allPermissionsGranted()) {
                Log.d(TAG, "Permissions granted")
            } else {
                Log.e(TAG, "Permissions not granted")
            }
        }

    fun allPermissionsGranted(): Boolean = requiresPermission.all {
        ContextCompat.checkSelfPermission(
            activity.baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        requestMultiplePermissionsLauncher.launch(requiresPermission)
    }

    companion object {
        private const val TAG = "PermissionsManager"
    }
}
