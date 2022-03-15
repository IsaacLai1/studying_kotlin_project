package com.example.test.plt_reg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.test.R
import android.content.Intent
import com.example.test.databinding.ActivityFaceRecogBinding
import com.example.test.databinding.ActivityPlantRecogBinding

class PlantRecogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantRecogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRecogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "Plant Detector"

        binding.pltRegAPI.setOnClickListener {
            val intent = Intent(this, PltRcResultActivity::class.java)
            val mode = 1
            intent.putExtra("processingCode", mode)
            startActivity(intent)
        }
        binding.pltRegTensor.setOnClickListener {
            val intent = Intent(this, PltRcResultActivity::class.java)
            val mode = 2
            intent.putExtra("processingCode", mode)
            startActivity(intent)
        }
    }
}