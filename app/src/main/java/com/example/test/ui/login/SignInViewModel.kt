package com.example.test.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
    var userEmailLiveData = MutableLiveData<String>("")
    var userPassLiveData = MutableLiveData<String>("")
    var userPassAgainLiveData = MutableLiveData<String>("")

    fun isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userEmailLiveData.value).matches()
    }

    fun isPassValid(): Boolean {
        return (userPassLiveData.value!!.length > 5)
    }

    fun isPassValidCorrect(): Boolean {
        return (userPassLiveData.value.equals(userPassAgainLiveData.value!!))
    }

    fun isInforValid(): Boolean {
        return (isEmailValid() && isPassValid() && isPassValidCorrect())
    }


}
