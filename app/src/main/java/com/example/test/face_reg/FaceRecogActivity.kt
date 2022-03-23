package com.example.test.face_reg

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.R
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import com.example.test.databinding.ActivityFaceRecogBinding
import com.example.test.databinding.ActivityFaceRecogGalBinding
import java.io.IOException

class FaceRecogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceRecogBinding
    private var imageUri: Uri? = null

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