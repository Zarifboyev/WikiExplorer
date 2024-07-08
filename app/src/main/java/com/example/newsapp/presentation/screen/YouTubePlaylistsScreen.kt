package com.example.newsapp.presentation.screen
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.data.model.CONST
import com.example.newsapp.databinding.ScreenYoutubePlaceholderBinding
import com.example.newsapp.presentation.adapters.VideoAdapter
import com.example.newsapp.presentation.viewModels.YouTubeViewModel
import com.example.newsapp.utils.NetworkManager
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
        checkNetworkAndHandleUI()
    }

    private fun fetchYouTubeData() {
        Timber.d("Fetching YouTube data...")
        lifecycleScope.launch {
            try {
                viewModel.fetchPlaylists(YOUTUBE_API_KEY, YOUTUBE_CHANNEL_ID)
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch YouTube data {${e.toString()}}")
            }
        }
    }

    private fun checkNetworkAndHandleUI() {

    }

    private fun onFastNetwork() {
        Timber.d("Fast network detected. Setting up observers and fetching data.")
        setupObservers()
        fetchYouTubeData()
    }

    private fun showNoNetworkUI() {
        Timber.d("No network available. Showing no network UI.")
        Toast.makeText(
            requireContext(),
            getString(R.string.no_internet_connection),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showSlowOrInsufficientNetworkConnection(s: String) {
        Timber.d("Slow or insufficient network detected: $s")
        Toast.makeText(requireContext(), getString(R.string
            .slow_or_insufficient_network), Toast.LENGTH_LONG)
            .show()
    }

    private fun setupObservers() {
        Timber.d("Setting up observers for YouTube playlists.")
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlists.takeIf { it.isNotEmpty() }?.let {
                binding.chipGroup.removeAllViews()
                it.forEachIndexed { index, playlist ->
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
        }

        viewModel.playlistItems.observe(viewLifecycleOwner) { playlistItems ->
            Timber.d("Playlist items updated: ${playlistItems.size} items")
            videoAdapter.submitList(playlistItems)
            binding.linearProgressIndicator.visibility = View.GONE
        }

        viewModel.videoStatistics.observe(viewLifecycleOwner) { videoStatistics ->
            Timber.d("Video statistics updated.")
            videoAdapter.updateVideoStatistics(videoStatistics)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Timber.e(errorMessage)
            binding.linearProgressIndicator.visibility = View.GONE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchPlaylistItems(playlistId: String) {
        Timber.d("Fetching playlist items for playlist ID: $playlistId")
        binding.linearProgressIndicator.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchPlaylistItems(YOUTUBE_API_KEY, playlistId)
        }
    }

    private fun openYouTubeVideo(videoId: String) {
        Timber.d("Opening YouTube video with ID: $videoId")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
            putExtra("force_fullscreen", true)
        }
        startActivity(intent)
    }
}
