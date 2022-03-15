import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.test.R
import com.example.test.databinding.FragmentPlayerViewBinding
import com.example.test.music_app.viewmodel.SongViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.*

class MusicGenFragment : Fragment() {
    private var _binding: FragmentPlayerViewBinding? = null
    private val binding get() = _binding!!
    var songViewModel: SongViewModel? = null
    var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentPlayerViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songViewModel = ViewModelProvider(requireActivity()).get(SongViewModel::class.java)
        binding.backBtn.setOnClickListener { requireActivity().onBackPressed() }
        gettingPlayer()
    }

    private fun gettingPlayer() {
        songViewModel?.getPlayer()?.observe(requireActivity()) { livePlayer ->
            if (livePlayer != null) {
                player = livePlayer
                playerControls(player!!)
            }
        }
    }

    private fun playerControls(player: ExoPlayer) {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                assert(mediaItem != null)
                binding.titleView.text = mediaItem!!.mediaMetadata.title
                binding.progressDuration.text = getReadableTime(player.currentPosition.toInt())
                binding.seekBar.progress = player.currentPosition.toInt()
                binding.totalDuration.text = getReadableTime(player.duration.toInt())
                binding.seekBar.max = player.duration.toInt()
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.artworkView.animation = loadRotation()
                if (!player.isPlaying) player.play()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    binding.titleView.text = Objects.requireNonNull(player.currentMediaItem)?.mediaMetadata?.title
                    binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
                    binding.progressDuration.text = getReadableTime(player.currentPosition.toInt())
                    binding.seekBar.progress = player.currentPosition.toInt()
                    binding.totalDuration.text = getReadableTime(player.duration.toInt())
                    binding.seekBar.max = player.duration.toInt()
                    showCurrentArtwork()
                    updatePlayerPositionProgress()
                    binding.artworkView.animation = loadRotation()
                } else {
                    binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle)
                }
            }
        })

        if (player.isPlaying) {
            binding.titleView.text = Objects.requireNonNull(player.currentMediaItem)?.mediaMetadata?.title
            binding.progressDuration.text = getReadableTime(player.currentPosition.toInt())
            binding.seekBar.progress = player.currentPosition.toInt()
            binding.totalDuration.text = getReadableTime(player.duration.toInt())
            binding.seekBar.max = player.duration.toInt()
            binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
            showCurrentArtwork()
            updatePlayerPositionProgress()
            binding.artworkView.animation = loadRotation()
        }

        binding.nextBtn.setOnClickListener(View.OnClickListener { view: View? ->
            if (player.hasNextMediaItem()) {
                player.seekToNext()
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.artworkView.animation = loadRotation()
            }
        })

        binding.prevBtn.setOnClickListener(View.OnClickListener {
            if (player.hasPreviousMediaItem()) {
                player.seekToPrevious()
                showCurrentArtwork()
                updatePlayerPositionProgress()
                binding.artworkView.animation = loadRotation()
            }
        })

        binding.playPauseBtn.setOnClickListener(View.OnClickListener {
            if (player.isPlaying) {
                player.pause()
                binding.playPauseBtn.setImageResource(R.drawable.ic_play_circle)
                binding.artworkView.clearAnimation()
            } else {
                player.play()
                binding.playPauseBtn.setImageResource(R.drawable.ic_pause_circle)
                binding.artworkView.setAnimation(loadRotation())
            }
        })

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressValue = 0
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                progressValue = seekBar.progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.progress = progressValue
                binding.progressDuration.setText(getReadableTime(progressValue))
                player.seekTo(progressValue.toLong())
            }
        })
    }

    private fun loadRotation(): Animation? {
        val rotateAnimation = RotateAnimation(
            0F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 10000
        rotateAnimation.repeatCount = Animation.INFINITE
        return rotateAnimation
    }

    private fun updatePlayerPositionProgress() {
        Handler().postDelayed({
            if (player!!.isPlaying) {
                binding.progressDuration.text = getReadableTime(player!!.currentPosition.toInt())
                binding.seekBar.progress = player!!.currentPosition.toInt()
            }
            updatePlayerPositionProgress()
        }, 1000)
    }

    private fun showCurrentArtwork() {
        binding.artworkView.setImageURI(Objects.requireNonNull(player!!.currentMediaItem)?.mediaMetadata?.artworkUri)
        binding.blurBg.setImageURI(Objects.requireNonNull(player!!.currentMediaItem)?.mediaMetadata?.artworkUri)
        if (binding.artworkView.drawable == null) {
            binding.artworkView.setImageResource(R.drawable.background_music_main)
            binding.blurBg.setImageResource(R.drawable.background_music_main)
        }
    }

    fun getReadableTime(totalDuration: Int): String? {
        val time: String
        val hrs = totalDuration / (1000 * 60 * 60)
        val min = totalDuration % (1000 * 60 * 60) / (1000 * 60)
        val secs = totalDuration % (1000 * 60 * 60) % (1000 * 60 * 60) % (1000 * 60) / 1000
        time = if (hrs < 1) {"$min:$secs"}
        else {"$hrs:$min:$secs"}
        return time
    }
}
