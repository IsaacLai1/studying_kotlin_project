package com.example.test.music_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer

class SongViewModel : ViewModel() {
    private val player: MutableLiveData<ExoPlayer> = MutableLiveData<ExoPlayer>()

    fun getPlayer(): MutableLiveData<ExoPlayer>? {
        return player
    }
}