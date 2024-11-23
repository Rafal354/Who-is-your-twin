package com.example.whosyourtwin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val goToMainButton: Button = findViewById(R.id.goToMainButton)

        goToMainButton.setOnClickListener {
            val intent = Intent(this, Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}
