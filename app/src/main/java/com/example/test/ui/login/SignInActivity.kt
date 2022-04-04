package com.example.test.ui.login

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.test.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySignInBinding
    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setAction()
    }

    fun initView() {

        binding.viewModel = viewModel

        viewModel.userEmailLiveData.observe(this) {
            if (!viewModel.isEmailValid()) {
                binding.edtMail.error = "Malformed"
            }
        }

        viewModel.userPassLiveData.observe(this) {
            if (!viewModel.isPassValid()) {
                binding.edtPass.error = "Pass more than 6 letter"
            }
        }
        viewModel.userPassAgainLiveData.observe(this) {
            if (!viewModel.isPassValidCorrect()) {
                binding.edtPassAgain.error = "Pass incorrect"
            }
        }
    }

    fun setAction() {
        binding.cvRegister.setOnClickListener {
            // check input
            if (viewModel.isInforValid()) {
                register()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    fun register() {

        val mail = binding.edtMail.text
        val pass = binding.edtPass.text

        firebaseAuth.createUserWithEmailAndPassword(mail.toString(), pass.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    Toast.makeText(this, "Sign up success!", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }

}