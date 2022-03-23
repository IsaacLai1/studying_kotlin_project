package com.example.test.music_app


import MusicGenFragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.test.R
import com.example.test.databinding.ActivityMusicGenBinding
import com.example.test.music_app.adapter.SongsAdapter
import com.example.test.music_app.songmodel.Song
import com.example.test.music_app.viewmodel.SongViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.*

class MusicGenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicGenBinding
    var storagePermissionLauncher: ActivityResultLauncher<String>? = null
    var songsAdapter: SongsAdapter? = null
    var gridSpanSize = 1
    var exoPlayer: ExoPlayer? = null
    var songViewModel: SongViewModel? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicGenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.playingSongNameView.isSelected = true
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL

        songViewModel = ViewModelProvider(this)[SongViewModel::class.java]
        songViewModel!!.getPlayer()!!.value = exoPlayer

        supportActionBar?.title = "Music Application"

        storagePermissionLauncher = registerForActivityResult(
            RequestPermission()
        ) { result: Boolean ->
            if (result) {
                fetchSongs()
            } else {
                respondOnUserPermissionActs()
            }
        }
        storagePermissionLauncher!!.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        eventHandle()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val position = exoPlayer?.currentPosition
        position?.let {
            outState.putLong("player", position)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        exoPlayer?.seekTo(
            savedInstanceState.getLong("player")
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        if (exoPlayer!!.isPlaying) {
            exoPlayer!!.stop()
        }
        exoPlayer!!.release()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun respondOnUserPermissionActs() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                fetchSongs()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(this)
                    .setTitle("Requesting Permission")
                    .setMessage("Allow us to fetch & show songs on your device")
                    .setPositiveButton("Allow ") { _, _ ->
                        storagePermissionLauncher!!.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                    .setNegativeButton("Don't Allow") { dialogInterface, _ ->
                        Toast.makeText(
                            applicationContext, " You denied to fetch songs",
                            Toast.LENGTH_LONG
                        ).show()
                        dialogInterface.dismiss()
                    }
                    .show()
            }
            else -> {
                Toast.makeText(this, "You denied to fetch songs", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchSongs() {
        val songs: MutableList<Song> = java.util.ArrayList()
        val songLibraryUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        contentResolver.query(songLibraryUri, projection, null, null, sortOrder).use { cursor ->

            val idCursor = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCursor = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val durationCursor = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeCursor = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val albumIdCursor = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCursor)
                var name = cursor.getString(titleCursor)
                val duration = cursor.getInt(durationCursor)
                val size = cursor.getInt(sizeCursor)
                val albumId = cursor.getLong(albumIdCursor)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                val albumartUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                val song = Song(id, uri, name, duration, size.toLong(), albumId, albumartUri)
                songs.add(song)
            }
            showSongs(songs)
        }
    }

    private fun showSongs(songs: List<Song>) {
        val layoutManager = GridLayoutManager(this, gridSpanSize)
        binding.recyclerview.layoutManager = layoutManager
        songsAdapter = SongsAdapter(songs, exoPlayer!!)
        binding.recyclerview.adapter = songsAdapter
    }

    private fun eventHandle() {
        binding.controlsWrapper.setOnClickListener {
            val playerViewFragment = MusicGenFragment()
            val fragmentTag: String = playerViewFragment::class.qualifiedName!!
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment_container, playerViewFragment)
                .addToBackStack(fragmentTag)
                .commit()
        }

        binding.nextBtn.setOnClickListener(View.OnClickListener {
            if (exoPlayer!!.hasNextMediaItem()) {
                exoPlayer!!.seekToNext()
            }
        })

        binding.playPauseBtn.setOnClickListener(View.OnClickListener {
            if (exoPlayer!!.isPlaying) {
                exoPlayer!!.pause()
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle)
            } else {
                if (exoPlayer!!.mediaItemCount > 0) {
                    exoPlayer!!.play()
                    binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
                }
            }
        })

        binding.prevBtn.setOnClickListener {
            if (exoPlayer!!.hasPreviousMediaItem()) {
                exoPlayer!!.seekToPrevious()
            }
        }
        transitionHandler()
    }

    private fun transitionHandler() {
        exoPlayer!!.addListener(object : Player.Listener {
            override fun onMediaItemTransition(songData: MediaItem?, reason: Int) {
                assert(songData != null)
                binding.playingSongNameView.text = songData!!.mediaMetadata.title
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    binding.playingSongNameView.text =
                        Objects.requireNonNull(exoPlayer!!.currentMediaItem)?.mediaMetadata?.title
                    binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.listview) {
            gridSpanSize =
                if (gridSpanSize == 1) {
                    2
                } else {
                    1
                }
        }
        val layoutManager = (binding.recyclerview.layoutManager as GridLayoutManager)
        layoutManager.spanCount = gridSpanSize
        binding.recyclerview.layoutManager = layoutManager
        songsAdapter!!.notifyDataSetChanged()
        return true
    }
}



