package com.example.newsapp.presentation.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.data.model.CONST
import com.example.newsapp.databinding.ScreenYoutubePlaceholderBinding
import com.example.newsapp.presentation.adapters.VideoAdapter
import com.example.newsapp.presentation.viewModels.YouTubeViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import timber.log.Timber

class YouTubePlaylistsScreen : Fragment(R.layout.screen_youtube_placeholder) {

    private val viewModel: YouTubeViewModel by viewModels()
    private lateinit var binding: ScreenYoutubePlaceholderBinding
    private val YOUTUBE_API_KEY = CONST.YOUTUBE_API_KEY
    private val YOUTUBE_CHANNEL_ID = CONST.YOUTUBE_CHANNEL_ID
    private lateinit var videoAdapter: VideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScreenYoutubePlaceholderBinding.bind(view)
        binding.linearProgressIndicator.visibility = View.VISIBLE
        videoAdapter = VideoAdapter { videoId -> openYouTubeVideo(videoId) }
        binding.youtubeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }

        setupObservers()

        lifecycleScope.launch {
            viewModel.fetchPlaylists(YOUTUBE_API_KEY, YOUTUBE_CHANNEL_ID)
        }
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            binding.chipGroup.removeAllViews()
            playlists.forEachIndexed { index, playlist ->
                val chip = Chip(context).apply {
                    text = playlist.snippet.title
                    isCheckable = true
                    setOnClickListener {
                        if (isChecked) {
                            fetchPlaylistItems(playlist.id)
                        }
                    }
                }
                binding.chipGroup.addView(chip)

                // Automatically select the first chip
                if (index == 0) {
                    chip.isChecked = true
                    fetchPlaylistItems(playlist.id)
                }
            }
        }

        viewModel.playlistItems.observe(viewLifecycleOwner) { playlistItems ->
            videoAdapter.submitList(playlistItems)
            binding.linearProgressIndicator.visibility = View.GONE
        }

        viewModel.videoStatistics.observe(viewLifecycleOwner) { videoStatistics ->
            videoAdapter.updateVideoStatistics(videoStatistics)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Timber.tag("YouTubeViewModel").e(errorMessage)
            binding.linearProgressIndicator.visibility = View.GONE
        }
    }

    private fun fetchPlaylistItems(playlistId: String) {
        binding.linearProgressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchPlaylistItems(YOUTUBE_API_KEY, playlistId)
        }
    }

    private fun openYouTubeVideo(videoId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        intent.putExtra("force_fullscreen", true)
        startActivity(intent)
    }


}
