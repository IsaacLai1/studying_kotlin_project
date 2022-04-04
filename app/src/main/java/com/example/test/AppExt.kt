package com.example.test

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


fun <T> Fragment.observer(liveData: LiveData<T>, onChange: (T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(onChange))
}

//fun <T> Activity.observer(liveData: LiveData<T>, onChange: (T?) -> Unit) {
//    liveData.observe(viewL, Observer(onChange))
//}
