package com.example.test.music_app.songmodel

import android.net.Uri

data class Song(
    val ID: Long,
    val URI: Uri?,
    val NAME: String?,
    val DURATION: Int,
    val SIZE: Long,
    val ALBUMID: Long,
    val ALBUMARTURI: Uri?)