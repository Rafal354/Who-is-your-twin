package com.example.whosyourtwin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        val goToMainButton: Button = findViewById(R.id.goToMainButton)
        Log.d(TAG, "goToMainButton initialized")

        goToMainButton.setOnClickListener {
            Log.d(TAG, "goToMainButton clicked")
            val intent = Intent(this, DisplayImageActivity::class.java)
            startActivity(intent)
            Log.d(TAG, "Navigating to DisplayImageActivity")
            finish()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
