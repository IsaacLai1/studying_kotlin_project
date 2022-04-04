package com.example.test.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.test.MainActivity
import com.example.test.R
import com.example.test.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onInitView()
        onViewClick()
    }

    fun onInitView() {

        binding.edtMail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!emailValid()) {
                    binding.edtMail.error = "Malformed"
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edtPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!passValid()) {
                    binding.edtPass.error = "Pass more than 6 letter"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    fun onViewClick() {
        binding.layoutRegister.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.tvLogin.setOnClickListener {
            if (checkInput()) login()
        }
    }

    private fun checkInput(): Boolean {
        return emailValid() && passValid()
    }

    private fun login() {
        val mail = binding.edtMail.text.toString()
        val pass = binding.edtPass.text.toString()

        binding.flLoading.visibility = View.VISIBLE
        viewModel.login(mail, pass,
            onSuccess = {
                binding.flLoading.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }, onFailure = {
                binding.flLoading.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            })
    }

    fun emailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(binding.edtMail.text)
            .matches() && binding.edtMail.text.isNotEmpty()
    }

    fun passValid(): Boolean {
        return (binding.edtPass.text.length > 5) && binding.edtPass.text.isNotEmpty()
    }

}
