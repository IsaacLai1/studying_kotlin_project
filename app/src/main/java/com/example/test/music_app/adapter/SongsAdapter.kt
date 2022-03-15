package com.example.test.music_app.adapter

import MusicGenFragment
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ActivityMusicGenRsItemBinding
import com.example.test.music_app.MusicGenActivity
import com.example.test.music_app.songmodel.Song
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import java.text.DecimalFormat
import java.util.*

class SongsAdapter(songs: List<Song>, mplayer: ExoPlayer) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var songs: List<Song>? = songs
    var mExoPlayer: ExoPlayer? = mplayer

    class SongViewHolder(val binding: ActivityMusicGenRsItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ActivityMusicGenRsItemBinding.
        inflate(LayoutInflater.from(parent.context),
            parent,false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val songItemHolder = holder as SongViewHolder
        val pos: Int = position
        val currentsong = songs!![position]
        songItemHolder.binding.title.text = currentsong.NAME
        songItemHolder.binding.size.text = sizeCalculation(currentsong.SIZE)
        songItemHolder.binding.number.text = (position+1).toString()
        songItemHolder.binding.duration.text = durationCalculation(currentsong.DURATION)

        val albumartUri: Uri? = currentsong.ALBUMARTURI
        if (albumartUri != null) {
            songItemHolder.binding.albumart.setImageURI(albumartUri)
            if (songItemHolder.binding.albumart.drawable == null) {
                songItemHolder.binding.albumart.setImageResource(R.drawable.background_music_main)
            }
        } else {
            songItemHolder.binding.albumart.setImageResource(R.drawable.background_music_main)
        }

        songItemHolder.binding.rowItemLayout.setOnClickListener { view ->
            getSong(currentsong)
            if (!mExoPlayer!!.isPlaying) {
                mExoPlayer!!.setMediaItems(getSongObjects(), pos, 0)
            } else {
                mExoPlayer!!.pause()
                mExoPlayer!!.seekTo(pos, 0)
            }
            mExoPlayer!!.prepare()
            mExoPlayer!!.play()
            Toast.makeText(view.context, "Playing: " + currentsong.NAME, Toast.LENGTH_LONG).show()
            startFragment(view.context)
        }
    }

    override fun getItemCount(): Int {
        return songs!!.size
    }

    fun getSongObjects(): MutableList<MediaItem> {
        val mediaItems: MutableList<MediaItem> = ArrayList()
        for (song in songs!!) {
            val mediaItem: MediaItem = MediaItem.Builder()
                .setUri(song.URI)
                .setMediaMetadata(getSongData(song))
                .build()
            mediaItems.add(mediaItem)
        }
        return mediaItems
    }

    private fun getSong(song: Song): MediaItem? {
        return MediaItem.Builder()
            .setUri(song.URI)
            .setMediaMetadata(getSongData(song))
            .build()
    }

    private fun getSongData(song: Song): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(song.NAME)
            .setArtworkUri(song.ALBUMARTURI)
            .build()
    }


    private fun startFragment(context: Context) {
        val playerViewFragment = MusicGenFragment()
        val fragmentTag: String? = playerViewFragment::class.qualifiedName
        (context as MusicGenActivity).supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_container, playerViewFragment)
            .addToBackStack(fragmentTag)
            .commit()
    }


    @SuppressLint("DefaultLocale")
    private fun durationCalculation(totalDuration: Int): String {
        val totalDurationText: String
        val hours = totalDuration / (1000 * 60 * 60)
        val minutes = totalDuration % (1000 * 60 * 60) / (1000 * 60)
        val seconds = totalDuration % (1000 * 60 * 60) % (1000 * 60 * 60) % (1000 * 60) / 1000
        totalDurationText = if (hours < 1) {
            String.format("%02d:%02d", minutes, seconds)
        } else {
            String.format("%1d:%02d:%02d", hours, minutes, seconds)
        }
        return totalDurationText
    }

    private fun sizeCalculation(bytes: Long): String {
        val hrSize: String
        val k = bytes / 1024.0
        val m = bytes / 1024.0 / 1024.0
        val g = bytes / 1024.0 / 1024.0 / 1024.0
        val t = bytes / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) { dec.format(t) + " TB"}
                 else if (g > 1) { dec.format(g) + " GB"}
                 else if (m > 1) { dec.format(m) + " MB"}
                 else if (k > 1) { dec.format(k) + " KB"}
                 else { dec.format(bytes.toDouble()) + " Bytes"}
        return hrSize
    }
}