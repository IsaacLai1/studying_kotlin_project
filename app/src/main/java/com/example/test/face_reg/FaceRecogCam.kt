package com.example.test.face_reg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.test.databinding.ActivityFaceRecogCamBinding
import com.example.test.face_reg.camera_processing.CamController

class FaceRecogCam : AppCompatActivity() {
    private lateinit var binding: ActivityFaceRecogCamBinding
    private lateinit var cameraControl: CamController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceRecogCamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initCameraController()
        checkCameraPermission()
        flipCamera()
    }

    private fun checkCameraPermission() {
        if (allPermissionsGranted()) {
            cameraControl.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraControl.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun flipCamera() {
        binding.btnSwitch.setOnClickListener {
            cameraControl.changeCameraSelector()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initCameraController() {
        cameraControl = CamController(
            this,
            binding.previewScreen,
            this,
            binding.faceRectangular
        )
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }
}