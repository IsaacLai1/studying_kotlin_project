package com.example.test.face_reg

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.test.R
import com.example.test.databinding.ActivityFaceRecogGalBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.io.FileDescriptor
import java.io.IOException

class FaceRecogGal : AppCompatActivity() {
    private lateinit var binding: ActivityFaceRecogGalBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceRecogGalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.title = "Face Recognition"

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData: Intent? = result.data
                imageUri = resultData?.data
                binding.previewFaceRecog.setImageURI(imageUri)

                if (resultData != null){
                    try {
                        convertToBitmap(imageUri!!)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        resultLauncher.launch(gallery)

        binding.buttonLoadPicture.setOnClickListener {
            resultLauncher.launch(gallery)
        }
    }

    @Throws(IOException::class)
    private fun convertToBitmap(uri: Uri) {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        showResult(fileDescriptor)
    }

    private fun showResult(fileDescriptor: FileDescriptor?) {
        val resultView: ImageView = findViewById(R.id.preview_face_recog)
        val selection = BitmapFactory.Options()
        selection.inMutable = true

        val bitmapImage = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        val regconization = Paint()
        regconization.strokeWidth = 3f
        regconization.color = Color.WHITE
        regconization.style = Paint.Style.STROKE

        val tempBitmap = Bitmap.createBitmap(bitmapImage.width, bitmapImage.height, Bitmap.Config.RGB_565)
        val drawCanvas = Canvas(tempBitmap)
        drawCanvas.drawBitmap(bitmapImage, 0f, 0f, null)

        val faceDetectorOption = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(faceDetectorOption)
        val image = InputImage.fromBitmap(bitmapImage, 0)

        detector.process(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    val rectF = face.boundingBox
                    drawCanvas.drawRect(rectF, regconization)
                    resultView.setImageDrawable(BitmapDrawable(resources, tempBitmap))
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}