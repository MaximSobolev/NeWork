package ru.netology.nework.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentMediaBinding
import ru.netology.nework.dto.AttachType
import ru.netology.nework.dto.Attachment

class MediaFragment : Fragment() {

    private lateinit var binding: FragmentMediaBinding

    private val args: MediaFragmentArgs by navArgs()

    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var playBackPosition = 0L
    private var attach: Attachment? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBinding(inflater, container)

        attach = args.attach
        if (attach == null) {
            findNavController().navigateUp()
        }

        setupVisibility()
        setupListeners()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if (attach?.type == AttachType.VIDEO) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (attach?.type == AttachType.VIDEO) {
            hideSystemUi()
            if (player == null) {
                initPlayer()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (attach?.type == AttachType.VIDEO) {
            releasePlayer()
        }
    }

    private fun setupBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentMediaBinding.inflate(
            inflater,
            container,
            false
        )
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun setupVisibility() {
        binding.apply {
            attach?.let { attachment ->
                when (attachment.type) {
                    AttachType.IMAGE -> {
                        image.visibility = View.VISIBLE
                        videoView.visibility = View.GONE
                        setupPicture()
                    }
                    AttachType.VIDEO -> {
                        image.visibility = View.GONE
                        videoView.visibility = View.VISIBLE
                    }
                    AttachType.AUDIO -> findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupPicture() {
        Glide.with(binding.image)
            .load(attach?.url)
            .placeholder(R.drawable.baseline_downloading_24)
            .error(R.drawable.outline_error_outline_24)
            .timeout(10_000)
            .into(binding.image)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initPlayer() {
        attach?.let { attachment ->
            val trackSelector = DefaultTrackSelector(requireContext()).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }
            player = ExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .build()
                .also { exoPlayer ->
                    binding.videoView.player = exoPlayer

                    val mediaItem = MediaItem.fromUri(attachment.url)

                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(playBackPosition)
                    exoPlayer.prepare()
                }
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.videoView
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playBackPosition = exoPlayer.currentPosition
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

}