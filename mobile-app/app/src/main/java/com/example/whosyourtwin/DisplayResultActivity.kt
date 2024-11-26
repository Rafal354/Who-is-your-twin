package com.example.whosyourtwin

import NamePercentageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DisplayResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_result)

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, DisplayImageActivity::class.java)
            startActivity(intent)
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val results = intent.getSerializableExtra("result") as? List<NamePercentage> ?: emptyList()
        val sortedResults: List<NamePercentage> = results.sortedBy { it.percentage }.reversed()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NamePercentageAdapter(sortedResults)
    }
}
