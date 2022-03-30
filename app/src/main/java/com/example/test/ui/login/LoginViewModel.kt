package com.example.test.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private var firebaseAuth = FirebaseAuth.getInstance()

    fun login(
        email: String,
        pass: String,
        onSuccess: (msg: String) -> Unit,
        onFailure: (msg: String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess.invoke("Login success")
                } else {
                    onFailure.invoke("Login fail")
                }
            }
            .addOnFailureListener {
                it.message?.let { it1 -> onFailure.invoke(it1) }
            }
    }
}
