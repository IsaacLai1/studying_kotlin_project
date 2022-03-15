package com.example.test.plt_reg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecognitionListViewModel: ViewModel() {
    private val _recognitionList = MutableLiveData<List<Recognition>>()
    val recognitionList: LiveData<List<Recognition>> = _recognitionList

    fun updateData(recognitions: List<Recognition>){
        _recognitionList.postValue(recognitions)
    }
}

data class Recognition(val label:String, val confidence:Float) {
    override fun toString():String{
        return "$label / $probabilityString"
    }
    val probabilityString = String.format("%.1f%%", confidence * 100.0f)
}