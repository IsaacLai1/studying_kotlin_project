package com.example.test.face_reg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.R
import android.widget.Button
import com.example.test.databinding.ActivityFaceRecogBinding
import com.example.test.databinding.ActivityFaceRecogGalBinding

class FaceRecogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceRecogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceRecogBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "Face Recognition"

        binding.buttonFrgCamera.setOnClickListener {
            val intent = Intent(this, FaceRecogCam::class.java)
            startActivity(intent)
        }
        binding.buttonFrgFolder.setOnClickListener {
            val intent = Intent(this, FaceRecogGal::class.java)
            startActivity(intent)
        }
    }
}